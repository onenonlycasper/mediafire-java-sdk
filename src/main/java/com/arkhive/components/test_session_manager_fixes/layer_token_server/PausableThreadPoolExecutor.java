package com.arkhive.components.test_session_manager_fixes.layer_token_server;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class PausableThreadPoolExecutor extends ThreadPoolExecutor implements Pausable {
    private boolean isPaused;
    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unPaused = pauseLock.newCondition();

    public PausableThreadPoolExecutor(int poolSize, BlockingQueue<Runnable> workQueue) {
        super(poolSize, poolSize, 1, TimeUnit.SECONDS, workQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public PausableThreadPoolExecutor(int poolSize) {
        super(poolSize, poolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) unPaused.await();
        } catch (InterruptedException e) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unPaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }
}