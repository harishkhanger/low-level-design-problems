package lld.blockingqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadPoolTest {

    @Test
    @DisplayName("Runs every submitted task exactly once across the workers")
    void runsAllSubmittedTasks() throws InterruptedException {
        final int tasks = 1_000;
        ThreadPool pool = new ThreadPool(4, 16);
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            pool.submit(() -> {
                counter.incrementAndGet();
                done.countDown();
            });
        }

        assertTrue(done.await(5, TimeUnit.SECONDS), "all tasks should complete");
        assertEquals(tasks, counter.get());
        pool.shutdown();
        pool.awaitTermination();
    }

    @Test
    @DisplayName("A task that throws does not kill its worker")
    void throwingTaskDoesNotKillWorker() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1, 8);
        CountDownLatch afterThrow = new CountDownLatch(1);

        pool.submit(() -> {
            throw new RuntimeException("boom");
        });
        pool.submit(afterThrow::countDown);

        assertTrue(afterThrow.await(2, TimeUnit.SECONDS),
                "the single worker must survive the exception and run the next task");
        pool.shutdown();
        pool.awaitTermination();
    }

    @Test
    @DisplayName("Graceful shutdown drains every queued task before workers exit")
    void gracefulShutdownDrainsQueue() throws InterruptedException {
        final int tasks = 50;
        ThreadPool pool = new ThreadPool(2, 8);
        AtomicInteger completed = new AtomicInteger(0);

        for (int i = 0; i < tasks; i++) {
            pool.submit(() -> {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                completed.incrementAndGet();
            });
        }

        pool.shutdown();
        pool.awaitTermination();

        assertEquals(tasks, completed.get(), "no queued task may be abandoned on graceful shutdown");
    }

    @Test
    @DisplayName("shutdownNow terminates idle workers immediately")
    void shutdownNowTerminatesWorkers() throws InterruptedException {
        ThreadPool pool = new ThreadPool(3, 8);
        pool.shutdownNow();
        pool.awaitTermination();
    }

    @Test
    @DisplayName("submit after shutdown is rejected")
    void submitAfterShutdownRejected() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2, 8);
        pool.shutdown();
        assertThrows(IllegalStateException.class, () -> pool.submit(() -> {}));
        pool.awaitTermination();
    }
}
