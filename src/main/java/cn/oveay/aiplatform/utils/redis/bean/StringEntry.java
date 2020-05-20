package cn.oveay.aiplatform.utils.redis.bean;

import java.util.Objects;

/**
 * oveashop
 * 存放Redis String中键值和过期时间的对象
 *
 * @author OVAE
 * @version 1.0.3
 * CreateDate: 2019-08-27 07:12:12
 */
public class StringEntry {
    private String key;
    private String value;
    private int ttl;
    private RedisTTLTimeType timeType;

    /**
     * 构建用于Redis String类型数据的pipe项
     * @param key 键
     * @param value 值
     * @param ttl 过期时间
     * @param timeType 过期时间类型
     */
    StringEntry(String key, String value, int ttl, RedisTTLTimeType timeType) {
        this(key, value);
        this.ttl = ttl;
        this.timeType = timeType;
    }

    /**
     * 构建用于Redis String类型数据的pipe项
     * @param key 键
     * @param value 值
     * @param ttl 过期时间（默认为秒）
     */
    StringEntry(String key, String value, int ttl) {
        this(key, value);
        this.ttl = ttl;
        this.timeType = RedisTTLTimeType.SECONDS;
    }

    /**
     * 构建用于Redis String类型数据的pipe项
     * @param key 键
     * @param value 值
     */
    StringEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringEntry)) return false;
        StringEntry that = (StringEntry) o;
        return getTtl() == that.getTtl() &&
                getKey().equals(that.getKey()) &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(), getTtl());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public RedisTTLTimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(RedisTTLTimeType timeType) {
        this.timeType = timeType;
    }
}
