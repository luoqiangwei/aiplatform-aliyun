package cn.oveay.aiplatform.utils.redis.bean;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * oveashop
 * 交给用户使用的pipeline前置对象
 *
 * @author OVAE
 * @version 1.2.0
 * CreateDate: 2019-08-27 07:23:12
 */
public class RedisSetStringList {

    private ArrayList<StringEntry> pipeList;

    /**
     * 初始化Redis String类型数据流水线对象
     */
    public RedisSetStringList() {
        pipeList = new ArrayList<>();
    }

    /**
     * 初始化Redis String类型数据流水线对象
     * @param initialCapacity 初始大小
     */
    public RedisSetStringList(int initialCapacity) {
        pipeList = new ArrayList<>((int)(initialCapacity));
    }

    /**
     * 添加一条String set指令
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        pipeList.add(new StringEntry(key, value));
    }

    /**
     * 添加一条String set指令
     * @param key 键
     * @param value 值
     * @param ttl 过期时间
     */
    public void set(String key, String value, int ttl){
        pipeList.add(new StringEntry(key, value, ttl));
    }

    /**
     * 添加一条String set指令
     * @param key 键
     * @param value 值
     * @param ttl 过期时间
     * @param timeType 时间类型
     */
    public void set(String key, String value, int ttl, RedisTTLTimeType timeType){
        pipeList.add(new StringEntry(key, value, ttl, timeType));
    }

    /**
     * 清空缓冲区
     */
    public void clear() {
        pipeList.clear();
    }

    public ArrayList<StringEntry> getPipeList() {
        return pipeList;
    }
}
