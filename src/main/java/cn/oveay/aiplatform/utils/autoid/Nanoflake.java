package cn.oveay.aiplatform.utils.autoid;

import cn.oveay.aiplatform.utils.autoid.exception.NanoflakeException;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * @author QiangweiLuo
 * 用于产生分布式ID的一个类
 */
public class Nanoflake {
    // 使用信号量来进行加锁
    private static Semaphore semaphore = new Semaphore(1);
    // 设定的机器ID
    private static long workerID = 0;
    // 最大可用的机器ID
    private static long maxWorkerID = 2048L - 1L;
    // 机器ID偏移bit
    private static long workerMoveBit = 52L;

    private static volatile long currTime;

    private static volatile long currNum = 0;

    static {
        // 加载配置文件
        Properties props = new Properties();
        try {
            props.load(Nanoflake.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("配置文件读取失败");
        }
        if(props.getProperty("machine_id") != null){
            workerID = Integer.parseInt(props.getProperty("machine_id"));
        }
    }

    /**
     * 获取当前机器ID
     * @return
     */
    public long getWorkerID() {
        return workerID;
    }

    /**
     * 设置当前机器ID
     * @param workerID
     * @throws NanoflakeException
     */
    public void setWorkerID(long workerID) throws NanoflakeException {
        if(workerID < 0 || workerID > maxWorkerID) throw new NanoflakeException("workerID too big or too low");
        this.workerID = workerID;
    }

    /**
     * 获取基于纳秒的数字分布式ID
     * @return
     */
    public static long getNanoflakeNum(){
        long temp = 0;
        long currtime = System.currentTimeMillis();
        try {
            semaphore.acquire(1);
            if(currtime == currTime){
                ++currNum;
            }else{
                currNum = 0;
                currTime = currtime;
            }
        } catch (InterruptedException e) {
            System.err.println("异常中断!");
            return -1;
        }finally {
            semaphore.release(1);
        }

        int length = Long.toBinaryString(currtime).length();
        temp |= currtime;
        workerID = workerID << workerMoveBit;
        temp |= workerID;
        long len = (long) Math.pow(2, workerMoveBit - length);
        temp |= (currNum % len) << length;
        return temp;
    }

    /**
     * 获取基于纳秒的字符分布式ID
     * @return
     */
    public static String getNanoflake(){
        return  Long.toHexString(getNanoflakeNum());
    }
}
