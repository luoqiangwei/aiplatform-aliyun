package cn.oveay.aiplatform.utils.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * oveashop
 * Jedis数据库连接池工具类
 *
 * @author OVAE
 * @version 1.0.0
 * Date: 2019-08-26 17:45
 */
public class JedisPoolUtils {
    private static JedisPool jedisPool;
    private static Optional<String> password = Optional.empty();

    static {
        final int REDIS_MAX_IDLE = 5;
        final int REDIS_MIN_IDLE = 1;
        final int REDIS_MAX_TOTAL = 0;
        final String REDIS_HOST = "127.0.0.1";
        final int REDIS_PORT = 6379;

        InputStream inStream = JedisPoolUtils.class.getClassLoader().getResourceAsStream("application.properties");
        Optional<Integer> redisMaxIdle = Optional.empty();
        Optional<Integer> redisMinIdle = Optional.empty();
        Optional<Integer> redisMaxTotal = Optional.empty();
        Optional<String> redisHost = Optional.empty();
        Optional<Integer> redisPort = Optional.empty();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        if (inStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Object read = properties.get("redis.password");
            password = Optional.ofNullable(read == null ? null : read.toString());
            read = properties.get("redis.maxIdle");
            redisMaxIdle = Optional.ofNullable(read == null ? null : Integer.parseInt(read.toString()));
            read = properties.get("redis.minIdle");
            redisMinIdle = Optional.ofNullable(read == null ? null : Integer.parseInt(read.toString()));
            read = properties.get("redis.maxTotal");
            redisMaxTotal = Optional.ofNullable(read == null ? null : Integer.parseInt(read.toString()));
            read = properties.get("redis.host");
            redisHost = Optional.ofNullable(read == null ? null : read.toString());
            read = properties.get("redis.port");
            redisPort = Optional.ofNullable(read == null ? null : Integer.parseInt(read.toString()));

            jedisPoolConfig.setMaxIdle(redisMaxIdle.orElse(REDIS_MAX_IDLE));
            jedisPoolConfig.setMinIdle(redisMinIdle.orElse(REDIS_MIN_IDLE));
            jedisPoolConfig.setMaxTotal(redisMaxTotal.orElse(REDIS_MAX_TOTAL));
        }
        jedisPool = new JedisPool(jedisPoolConfig, redisHost.orElse(REDIS_HOST), redisPort.orElse(REDIS_PORT));
    }

    private JedisPoolUtils() {}

    /**
     * 获取Jedis连接
     * @return
     */
    public static Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        password.ifPresent(jedis::auth);
        return jedis;
    }
}
