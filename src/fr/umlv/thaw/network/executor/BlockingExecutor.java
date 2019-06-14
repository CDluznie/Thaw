package fr.umlv.thaw.network.executor;

import java.util.Objects;

import io.vertx.core.WorkerExecutor;

public class BlockingExecutor {

	private final WorkerExecutor executor;

	/**
	 * Construct a blocking executor.
	 * 
	 * @param 	executor a vertx worker executor
	 * @throws 	NullPointerException if executor is null
	 */
	public BlockingExecutor(WorkerExecutor executor) {
		this.executor = Objects.requireNonNull(executor);
	}
	
	/**
	 * Execute safely the specified blocking runnable.
	 * 
	 * @param 	blockingRunnable the specified runnable
	 * @throws 	NullPointerException if blockingRunnable is null
	 */
	public void execute(Runnable blockingRunnable) {
		Objects.requireNonNull(blockingRunnable);
		executor.executeBlocking(__ -> blockingRunnable.run(), __ -> {});
	}
	
}
