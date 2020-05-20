package cn.oveay.aiplatform.utils.redis.bean;

/**
 * oveashop
 * 描述Redis TTL的时间类型
 *
 * @author OVAE
 * @version 1.0.0
 * CreateDate: 2019-08-27 09:08:23
 */
public enum RedisTTLTimeType {
    SECONDS(0),  MINUTES(1);

    private int type;

    private RedisTTLTimeType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
