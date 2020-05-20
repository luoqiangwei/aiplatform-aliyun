package cn.oveay.aiplatform.utils.lock;

/**
 * oveashop
 * 定义锁的规范
 *
 * @author OVAE
 * @version 1.0.0
 * CreateDate: 2019-08-29 20:13:52
 */
public interface Lock {
    /**
     * 开锁
     */
    void lock();

    /**
     * 解锁
     */
    void unlock();
}
