package com.ecreditpal.maas.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author shuz
 */
public class WorkDispatcher {
    private final static Logger logger = LoggerFactory.getLogger(WorkDispatcher.class);

    private static final int DEFAULT_NUM_THREAD = 32;
    private static final int DEFAULT_CAPACITY = DEFAULT_NUM_THREAD * 4;
    private static final long DEFAULT_ALIVE_TIME = 60;

    private static volatile WorkDispatcher instance = null;
    private static volatile boolean initialized = false;
    private static final Object initLock = new Object();

    private ModelExecutor modelExecutor;

    //核心线程
    private Integer corePoolSize;
    //队列的大小
    private Integer maxPoolSize;
    //线程池使用的队列
    private BlockingQueue<Runnable> queue;
    //任务被拒绝时使用的policy
    private RejectedExecutionHandler handler;
    //线程存活时间
    private Long keepAliveTime;


    public WorkDispatcher(Builder builder) {
        if (builder == null) {
            builder = Builder.getDefault();
        }
        BlockingQueue<Runnable> queue = builder.getQueue();

        this.corePoolSize = builder.getCorePoolSize();
        this.maxPoolSize = builder.getMaxPoolSize();
        this.keepAliveTime = builder.getKeepAliveTime();
        this.queue = queue == null ? new LinkedBlockingQueue<Runnable>(DEFAULT_CAPACITY) : queue;
        this.handler = builder.getHandler();

        logger.info("Initialize work dispatcher");
        int numThread = DEFAULT_NUM_THREAD;
        int capacity = DEFAULT_CAPACITY;
        boolean userOrderedExecutor = false;


        this.modelExecutor = new ModelExecutor(corePoolSize, maxPoolSize, keepAliveTime, queue, handler);
        logger.info("Instantiate model executor with {} threads and", corePoolSize);
    }

    public WorkDispatcher() {
        this(Builder.getDefault());
    }


    public static WorkDispatcher getInstance(Builder builder) {
        if (initialized) {
            return instance;
        }
        synchronized (initLock) {
            if (!initialized) {
                instance = new WorkDispatcher(builder);
                initialized = true;
            }
            return instance;
        }
    }

    public static WorkDispatcher getInstance() {
        return getInstance(null);
    }

    public boolean isInitialized() {
        return initialized;
    }



    public void modelExecute(Runnable r) {
        if (!initialized) {
            logger.error("dispatcher is not initialized yet");
            return;
        }

        modelExecutor.execute(null, r);
    }




    /**
     * 设置线程池的各项参数
     */
    public static class Builder {
        //核心线程
        private Integer corePoolSize = Integer.valueOf("32");
        //队列的大小
        private Integer maxPoolSize = Integer.valueOf("100");
        //线程池使用的队列
        private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(Integer.valueOf("50"));
        //设置线程活跃时间
        private Long keepAliveTime = DEFAULT_ALIVE_TIME;
        //任务被拒绝时使用的policy
        private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        /**
         * 设置核心线程数
         *
         * @param corePoolSize 核心线程数
         * @return this
         */
        public Builder corePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        /**
         * 设置队列
         *
         * @param queue 自定义BlockingQueue
         * @return this
         */
        public Builder queue(BlockingQueue<Runnable> queue) {
            this.queue = queue;
            return this;
        }

        /**
         * 设置线程活跃时间
         * @param keepAliveTime 自定义线程活跃时间
         * @return this
         */
        public Builder keepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        /**
         * 设置handler
         *
         * @param handler 自定义handler
         * @return this
         */
        public Builder handler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }

        public WorkDispatcher build() {
            return WorkDispatcher.getInstance(this);
        }

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public BlockingQueue<Runnable> getQueue() {
            return queue;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public RejectedExecutionHandler getHandler() {
            return handler;
        }

        public static Builder getDefault() {
            return new Builder();
        }

        public Long getKeepAliveTime() {
            return keepAliveTime;
        }
    }

    public static void main(String[] args) {
        WorkDispatcher.Builder builder = new Builder();
        WorkDispatcher workDispatcher = builder.corePoolSize(12).queue(null).build();
    }

    public ModelExecutor getModelExecutor(){
        return  modelExecutor;
    }


}
