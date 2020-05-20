package cn.oveay.aiplatform.utils.redis.excption;

/**
 * oveashop
 * Redis TTL无效异常
 *
 * @author OVAE
 * @version 1.0.0
 * Date: 2019-08-26 18:12
 */
public class RedisInvalidExpireTimeException extends Exception {
    public RedisInvalidExpireTimeException() {
        super("ERR invalid expire time");
    }
}
