package lld.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool {

    private final BoundedBlockingQueue<Runnable> queue;
    private final List<Thread> workers = new ArrayList<>();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public ThreadPool(int poolSize, int queueCapacity) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("poolSize must be positive");
        }
        this.queue = new BoundedBlockingQueue<>(queueCapacity);
        for (int i = 0; i < poolSize; i++) {
            Thread worker = new Thread(this::runWorker, "pool-worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        if (shutdown.get()) {
            throw new IllegalStateException("pool is shut down; not accepting tasks");
        }
        queue.put(task);
    }

    private void runWorker() {
        while (true) {
            Runnable task;
            try {
                task = queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try {
                task.run();
            } catch (RuntimeException e) {
                // a task failing must not kill the worker
            }
        }
    }

    public void shutdown() {
        shutdown.set(true);
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (Thread worker : workers) {
            worker.join();
        }
    }
}
