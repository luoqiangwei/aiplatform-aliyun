package cn.oveay.aiplatform.utils.lock;

/**
 * oveashop
 * 读写锁规范定义
 *
 * @author OVAE
 * @version 1.0.0
 * CreateDate: 2019-08-29 20:14:54
 */
public interface ReadAndWriteLock {
    /**
     * 获取读锁
     * @return 读锁
     */
    Lock getReadLock();

    /**
     * 获取写锁
     * @return 写锁
     */
    Lock getWriteLock();
}
