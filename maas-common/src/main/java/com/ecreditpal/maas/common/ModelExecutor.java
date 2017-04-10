package com.ecreditpal.maas.common;

import com.google.common.base.Preconditions;

import java.util.concurrent.*;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/22.
 */
public class ModelExecutor implements CustomExecutor {

    final static RejectedExecutionHandler DEFAULT_REJECT_HANDLER = new ThreadPoolExecutor.DiscardPolicy();

    protected ExecutorService threadPool;
    protected RejectedExecutionHandler handler;


    public ModelExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, BlockingQueue<Runnable> queue, RejectedExecutionHandler handler) {
        Preconditions.checkArgument(corePoolSize > 0, "corePoolSize must be greater than 0");
        Preconditions.checkArgument(maxPoolSize > 0, "maxPoolSize must be greater than 0");
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue, handler);
    }


    @Override
    public void execute(Object orderingKey, Runnable r) {
        this.threadPool.execute(r);
    }

    @Override
    public void shutdown() {
        this.threadPool.shutdown();

    }

    @Override
    public boolean awaitTermination(long timeout) {
        try {
            this.threadPool.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // gracefully ignore interrupt
        }

        return threadPool.isTerminated();
    }

    @Override
    public long getTotalTaskCount() {
        return ((ThreadPoolExecutor) threadPool).getTaskCount();
    }

    @Override
    public long getQueuedTaskCount() {
        return ((ThreadPoolExecutor) threadPool).getQueue().size();
    }

    @Override
    public long getCompletedTaskCount() {
        return ((ThreadPoolExecutor) threadPool).getCompletedTaskCount();
    }


    public String toString() {
        return threadPool.toString();
    }

}
