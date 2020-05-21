package cn.oveay.aiplatform.utils.token;


import cn.oveay.aiplatform.utils.redis.JedisUtils;
import cn.oveay.aiplatform.utils.redis.bean.RedisSetStringList;
import cn.oveay.aiplatform.utils.redis.bean.RedisTTLTimeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * oveashop
 * token生成器
 *
 * @author OVAE
 * @version 0.2.0
 * CreateDate: 2019-08-28 08:17:47
 */
public class Token {
    private static RedisSetStringList setList = new RedisSetStringList(1000);
    private volatile static ArrayList<String> getList = new ArrayList<>(1000);

    // 读、写、内存回收、分配线程的数目
    private static int readThreadNumber = 1;
    private static int writeThreadNumber = 1;
    private static int gcThreadNumber = 1;

    // 读、写、内存回收、分类线程的停顿时间
    private static int readThreadTimeInterval = 500;
    private static int writeThreadTimeInterval = 500;
    private static int gcThreadTimeInterval = 500;

    // 读、写、内存回收、分类线程的编号系统
    private static int readNo = 0;
    private static int writeNo = 0;
    private static int gcNo = 0;

    // 结果集超时时间
    private static int resultGCTimeOut = 50000;

    // 结果集
    private static Vector<Vector<TokenResult>> resultSlots = new Vector<>(readThreadNumber * 2);
    // 存储从redis获取的数据
    private static HashSet<Integer> seatFlag = new HashSet<>(100);
    // 当前读线程使用的容器编号
    private volatile static Vector<Integer> readCurrSlotsNo = new Vector<>(readThreadNumber);

    // 设置自旋锁
    private static AtomicReference<Object> spinlock = new AtomicReference<>();
    private static final long SPINLOCK_TIME_OUT = 500;

    // 最大查询并发
    private static int maxCurrReadCacheNumber = 10000;
    private static Semaphore semaphore = new Semaphore(maxCurrReadCacheNumber);

    static {
        // 写线程
        for (int i = 0; i < writeThreadNumber; i++) {
            new Thread(() -> {
                int currNo = writeNo++;
                while (true) {
                    if (setList.getPipeList().size() != 0) {
                        JedisUtils.set(setList);
                        setList.clear();
                    }
                    try {
                        Thread.sleep(writeThreadTimeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        // 读Redis并且缓存到内存的线程
        for (int i = 0; i < readThreadNumber; i++) {
            new Thread(() -> {
                int currNo = readNo++;
                readCurrSlotsNo.add(0);
                int currSeat = 0;
                while (seatFlag.contains(currSeat)) currSeat++;
                seatFlag.add(currSeat);
                readCurrSlotsNo.set(currNo, currSeat);
                while (true) {
                    while (true) {
                        try {
                            if (lock(SPINLOCK_TIME_OUT)) break;
                        } catch (SpinLockExecption spinLockExecption) {
                            spinLockExecption.printStackTrace();
                        }
                    }
                    currSeat = readCurrSlotsNo.get(currNo);
                    int nextSeat = 0;
                    while (seatFlag.contains(nextSeat) || currSeat == nextSeat) nextSeat++;
                    seatFlag.add(nextSeat);
                    readCurrSlotsNo.set(currNo, nextSeat);
                    if (resultSlots.size() <= currSeat) {
                        resultSlots.add(new Vector<>());
                    }
                    List<String> list = JedisUtils.get(getList);
                    getList.clear();
                    for(String result : list) {
                        resultSlots.get(currSeat).add(new TokenResult(result));
                    }
                    try {
                        unlock();
                    } catch (SpinLockExecption spinLockExecption) {
                        spinLockExecption.printStackTrace();
                    }
                    try {
                        Thread.sleep(readThreadTimeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        // 回收线程
        for (int i = 0; i < gcThreadNumber; i++) {
            new Thread(() -> {
                int currNo = gcNo++;
                while (true) {
                    int currSlot = 0;
                    while (true) {
                        try {
                            if (lock(SPINLOCK_TIME_OUT)) break;
                        } catch (SpinLockExecption spinLockExecption) {
                            spinLockExecption.printStackTrace();
                        }
                    }
                    for (Vector<TokenResult> tokenResults : resultSlots) {
                        boolean isUnUse = true;
                        if (seatFlag.contains(currSlot) && readCurrSlotsNo.contains(currSlot)) {
                            isUnUse = false;
                            break;
                        } else {
                            for (TokenResult result : tokenResults) {
                                if (!result.getGetFlag()) {
                                    if (System.currentTimeMillis() - result.getCreateTime() < resultGCTimeOut) {
                                        isUnUse = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (isUnUse) {
                            tokenResults.clear();
                            seatFlag.remove(currSlot);
                        }
                        currSlot++;
                    }
                    try {
                        unlock();
                    } catch (SpinLockExecption spinLockExecption) {
                        spinLockExecption.printStackTrace();
                    }
                    try {
                        Thread.sleep(gcThreadTimeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private Token() {}

    /**
     * 内部使用的自旋锁，加锁
     * @param timeout
     * @return
     * @throws SpinLockExecption
     */
    private static boolean lock(long timeout) throws SpinLockExecption {
        Thread currThread = Thread.currentThread();
        if (spinlock.get() == currThread) {
            throw new SpinLockExecption("重复加锁！");
        }
        long startTime = System.currentTimeMillis();
        while (!spinlock.compareAndSet(null, currThread)) {
            if (System.currentTimeMillis() - startTime > timeout) {
                return false;
            }
            try {
                Thread.sleep(timeout / 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 内部使用的自旋锁，解锁
     * @throws SpinLockExecption
     */
    private static void unlock() throws SpinLockExecption {
        Thread currThread = Thread.currentThread();
        if (spinlock.get() != currThread) {
            throw new SpinLockExecption("当前未加锁！");
        }
        while (!spinlock.compareAndSet(currThread, null));
    }

    /**
     * 存数据方法
     * @param key 键
     * @param value 值
     */
    public static void set(String key, String value) {
        while (true) {
            try {
                if (lock(SPINLOCK_TIME_OUT)) break;
            } catch (SpinLockExecption spinLockExecption) {
                spinLockExecption.printStackTrace();
            }
        }
        setList.set(key, value);
        try {
            unlock();
        } catch (SpinLockExecption spinLockExecption) {
            spinLockExecption.printStackTrace();
        }
    }

    /**
     * 存数据方法
     * @param key 键
     * @param value 值
     * @param ttl 存活时间
     */
    public static void set(String key, String value, int ttl) {
        while (true) {
            try {
                if (lock(SPINLOCK_TIME_OUT)) break;
            } catch (SpinLockExecption spinLockExecption) {
                spinLockExecption.printStackTrace();
            }
        }
        setList.set(key, value, ttl);
        try {
            unlock();
        } catch (SpinLockExecption spinLockExecption) {
            spinLockExecption.printStackTrace();
        }
    }

    /**
     * 设置key-value，请会被发送线程发送过去
     * @param key 键
     * @param value 值
     * @param ttl 过期时间
     * @param timeType 时间类型
     */
    public static void set(String key, String value, int ttl, RedisTTLTimeType timeType) {
        while (true) {
            try {
                if (lock(SPINLOCK_TIME_OUT)) break;
            } catch (SpinLockExecption spinLockExecption) {
                spinLockExecption.printStackTrace();
            }
        }
        setList.set(key, value, ttl, timeType);
        try {
            unlock();
        } catch (SpinLockExecption spinLockExecption) {
            spinLockExecption.printStackTrace();
        }
    }

    /**
     * 获取数据预先处理
     * @param key 键
     * @return 返回位置对象
     */
    public static TokenLocation preCheck(String key) {
        try {
            while (true) {
                try {
                    if (lock(SPINLOCK_TIME_OUT)) break;
                } catch (SpinLockExecption spinLockExecption) {
                    spinLockExecption.printStackTrace();
                }
            }
            int curr = getList.size();
            getList.add(key);
            return new TokenLocation(readCurrSlotsNo.get(0), curr);
        } finally {
            try {
                unlock();
            } catch (SpinLockExecption spinLockExecption) {
                spinLockExecption.printStackTrace();
            }
        }
    }

    /**
     * 检查Token是否合法
     * @param key 检查的值
     * @param value 期望值
     * @return 检查结果
     */
    public static boolean check(String key, String value) {
        try {
            semaphore.acquire(1);
            TokenLocation tokenLocation = preCheck(key);
            try {
                Thread.sleep((long) (readThreadTimeInterval));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value.equals(get(tokenLocation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(1);
        }
        return false;
    }

    /**
     * 通过位置信息获取值
     * @param location 位置对象
     * @return 相应位置的值
     */
    private static String get(TokenLocation location) {
        TokenResult tokenResult;
        // 10次重试
        for (int i = 0; i < 10; i++) {
            if (resultSlots.size() > location.getSaveSlot()) {
                Vector<TokenResult> tokenResults = resultSlots.get(location.getSaveSlot());
                if (tokenResults.size() > location.getSaveSeat()) {
                    tokenResult = tokenResults.get(location.getSaveSeat());
                    if (tokenResult.getGetFlag()) {
                        return null;
                    }
                    tokenResult.setGetFlag(true);
                    return tokenResult.getResult();
                }
            }
            try {
                Thread.sleep((long) (readThreadTimeInterval * 0.5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 这个是禁止外用的异常类，和CAS锁一样
    private static class SpinLockExecption extends Exception {
        private SpinLockExecption(String msg) {
            super(msg);
        }
    }
}
