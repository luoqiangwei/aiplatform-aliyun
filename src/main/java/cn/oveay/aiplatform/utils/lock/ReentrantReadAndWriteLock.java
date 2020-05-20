package cn.oveay.aiplatform.utils.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * oveashop
 * 实现读写锁
 *
 * @author OVAE
 * @version 0.1.0
 * CreateDate: 2019-08-29 19:59:03
 */
public class ReentrantReadAndWriteLock implements ReadAndWriteLock {
    private final Lock writeLock = new WriteLock();
    private final Lock readLock = new ReadLock();

    @Override
    public Lock getReadLock() {
        return readLock;
    }

    @Override
    public Lock getWriteLock() {
        return writeLock;
    }

    private static class Sync extends AbstractQueuedSynchronizer {


        protected Sync() {
            super();
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return super.tryAcquire(arg);
        }

        @Override
        protected boolean tryRelease(int arg) {
            return super.tryRelease(arg);
        }

        @Override
        protected int tryAcquireShared(int arg) {
            return super.tryAcquireShared(arg);
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            return super.tryReleaseShared(arg);
        }

        @Override
        protected boolean isHeldExclusively() {
            return super.isHeldExclusively();
        }
    }

    private static class WriteLock implements Lock {
        Sync sync;

        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    }

    private static class ReadLock implements Lock {
        private final Sync sync;

        private ReadLock() {
            sync = new Sync();
        }

        private ReadLock(ReadLock readLock) {
            sync = readLock.sync;
        }

        @Override
        public void lock() {

        }

        @Override
        public void unlock() {

        }
    }

}
