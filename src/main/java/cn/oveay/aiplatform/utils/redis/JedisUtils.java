package cn.oveay.aiplatform.utils.redis;

import cn.oveay.aiplatform.utils.redis.bean.RedisSetStringList;
import cn.oveay.aiplatform.utils.redis.bean.RedisTTLTimeType;
import cn.oveay.aiplatform.utils.redis.bean.StringEntry;
import cn.oveay.aiplatform.utils.redis.utils.JedisPoolUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * oveashop
 * 对Jedis进一步进行封装
 *
 * @author OVAE
 * @version 0.4.1
 * Date: 2019-08-26 17:45
 */
public class JedisUtils {

    private final static ThreadPoolExecutor threadPool;

    static {
        int redisToolCorePoolSize = Runtime.getRuntime().availableProcessors() / 4;
        int redisToolMaxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        int redisToolKeepAliveTime = 10 * 60;
        int redisToolBlockingQueueSize = 1000;

        InputStream inStream = JedisUtils.class.getClassLoader().getResourceAsStream("application.properties");
        if (inStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(properties.getProperty("redis.tool.corePoolSize") != null){
                redisToolCorePoolSize = Integer.parseInt(properties.getProperty("redis.tool.corePoolSize"));
            }
            if(properties.getProperty("redis.tool.maxPoolSize") != null){
                redisToolMaxPoolSize = Integer.parseInt(properties.getProperty("redis.tool.maxPoolSize"));
            }
            if(properties.getProperty("redis.tool.keepAliveTime") != null){
                redisToolKeepAliveTime = Integer.parseInt(properties.getProperty("redis.tool.keepAliveTime"));
            }
            if(properties.getProperty("redis.tool.blockingQueueSize") != null){
                redisToolBlockingQueueSize = Integer.parseInt(properties.getProperty("redis.tool.blockingQueueSize"));
            }
        }

        threadPool = new ThreadPoolExecutor(redisToolCorePoolSize, redisToolMaxPoolSize,
                redisToolKeepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(redisToolBlockingQueueSize),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private JedisUtils() {}

    /**
     * 保存String键值对
     * @param key 键
     * @param value 值
     * @return 执行结构是否成功
     */
    public static boolean set(String key, String value) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                boolean result = jedis.set(key, value).equals("OK");
                jedis.close();
                return result;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存String键值对
     * @param key 键
     * @param value 值
     * @param ttl 过期时间，单位 秒
     * @return 执行结构是否成功
     */
    public static boolean set(String key, String value, int ttl) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                SetParams ex = SetParams.setParams().ex(ttl);
                boolean result = false;
                if (ttl > 0){
                    result = jedis.set(key, value, ex).equals("OK");
                } else {
                    result = jedis.set(key, value).equals("OK");
                }
                jedis.close();
                return result;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存String键值对
     * @param key 键
     * @param value 值
     * @param ttl 过期时间，单位 秒
     * @return 执行结构是否成功
     */
    public static boolean set(String key, String value, long ttl) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Transaction multi = jedis.multi();
                boolean reuslt = false;
                if (ttl > 0){
                    reuslt = multi.set(key, value, SetParams.setParams().px(ttl)).equals("OK");
                } else {
                    reuslt = multi.set(key, value).equals("OK");
                }
                multi.exec();
                jedis.close();
                return reuslt;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 流水线set String操作，建议最高以100000条执行。
     * @param list 流水线封装类
     * @return 执行结果集
     */
    public static List<Boolean> set(final RedisSetStringList list) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Pipeline pipelined = jedis.pipelined();
                for (StringEntry entry : list.getPipeList()) {
                    if (entry.getTtl() > 0) {
                        if(entry.getTimeType() == RedisTTLTimeType.SECONDS){
                            pipelined.set(entry.getKey(), entry.getValue(), SetParams.setParams().ex(entry.getTtl()));
                        }else if(entry.getTimeType() == RedisTTLTimeType.MINUTES){
                            pipelined.set(entry.getKey(), entry.getValue(), SetParams.setParams().px(entry.getTtl()));
                        }
                    }else {
                        pipelined.set(entry.getKey(), entry.getValue());
                    }
                }
                List<Boolean> results = new ArrayList<>(list.getPipeList().size());
                for(Object result : pipelined.syncAndReturnAll()){
                    results.add(result.equals("OK"));
                }
                pipelined.close();
                jedis.close();
                return results;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    /**
     * 通过流水线获取String数据类型的value值
     * @param list 流水线包装对象
     * @return 执行结果集合
     */
    public static List<String> get(final RedisSetStringList list) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Pipeline pipelined = jedis.pipelined();
                for (StringEntry key : list.getPipeList()){
                    pipelined.get(key.getKey());
                }
                List<String> results = new ArrayList<>(list.getPipeList().size());
                for (Object result : pipelined.syncAndReturnAll()){
                    results.add(result.toString());
                }
                pipelined.close();
                jedis.close();
                return results;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    /**
     * 通过key列表获取String数据类型的value值
     * @param keys key列表
     * @return 执行结果集合
     */
    public static List<String> get(final List<String> keys) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Pipeline pipelined = jedis.pipelined();
                for (String key : keys){
                    pipelined.get(key);
                }
                List<String> results = new ArrayList<>(keys.size());
                for (Object result : pipelined.syncAndReturnAll()){
                    results.add(result.toString());
                }
                pipelined.close();
                jedis.close();
                return results;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    /**
     * 获取Reids String类型key值对应的value
     * @param key 键值
     * @return value或者null
     */
    public static String get(String key) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                String value = jedis.get(key);
                jedis.close();
                return value;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除Redis数据，根据key值
     * @param key 被删除的键值
     * @return 是否成功删除
     */
    public static boolean del(String key) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                boolean result = jedis.del(key) == 1;
                jedis.close();
                return result;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 流水线删除key值
     * @param list 流水线对象
     * @return 删除结构
     */
    public static List<Boolean> del(RedisSetStringList list) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Pipeline pipelined = jedis.pipelined();
                for (StringEntry key : list.getPipeList()){
                    pipelined.del(key.getKey());
                }
                List<Boolean> results = new ArrayList<>(list.getPipeList().size());
                for(Object simpleResult : pipelined.syncAndReturnAll()){
                    if(simpleResult.equals(1)){
                        results.add(true);
                    }else {
                        results.add(false);
                    }
                }
                pipelined.close();
                jedis.close();
                return results;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    /**
     * 通过key列表删除key值
     * @param keys 被删除的key列表
     * @return 删除结构
     */
    public static List<Boolean> del(ArrayList<String> keys) {
        try {
            return threadPool.submit(() -> {
                Jedis jedis = JedisPoolUtils.getJedis();
                Pipeline pipelined = jedis.pipelined();
                for (String key : keys){
                    pipelined.del(key);
                }
                List<Boolean> results = new ArrayList<>(keys.size());
                for(Object simpleResult : pipelined.syncAndReturnAll()){
                    if(simpleResult.equals(1)){
                        results.add(true);
                    }else {
                        results.add(false);
                    }
                }
                pipelined.close();
                jedis.close();
                return results;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

}
