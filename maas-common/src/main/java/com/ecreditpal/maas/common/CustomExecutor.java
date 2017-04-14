package com.ecreditpal.maas.common;

/**
 * @author shuz
 */
public interface CustomExecutor {
	/**
	 * Custom thread pool Interface.
	 * 
	 * @param orderingKey Ordering key.
	 * @param r Runnable object.
	 */
	public abstract void execute(Object orderingKey, Runnable r);

	/**
	 * Shutdown.
	 */
	public abstract void shutdown();

	/**
	 * Wait gracefully for threads to terminate.
	 * 
	 * @param timeout Timeout in second.
	 * @return True if executor terminated.
	 */
	public abstract boolean awaitTermination(long timeout);

	public abstract long getTotalTaskCount();

	public abstract long getQueuedTaskCount();

	public abstract long getCompletedTaskCount();


}
