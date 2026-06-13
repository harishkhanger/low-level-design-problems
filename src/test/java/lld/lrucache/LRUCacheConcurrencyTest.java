package lld.lrucache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LRUCacheConcurrencyTest {

    private static final int CAPACITY = 50;
    private static final int THREADS = 500;

    @Test
    @DisplayName("Concurrent puts of distinct keys never exceed capacity")
    void concurrentPutsStayWithinCapacity() throws InterruptedException {
        LRUCache<Integer, Integer> cache = new LRUCache<>(CAPACITY);
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int key = i;
            pool.submit(() -> {
                try {
                    startGate.await();
                    cache.put(key, key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        done.await();
        pool.shutdown();

        assertEquals(CAPACITY, cache.size());
    }

    @Test
    @DisplayName("Mixed concurrent gets and puts keep the cache consistent")
    void concurrentGetsAndPuts() throws InterruptedException {
        LRUCache<Integer, Integer> cache = new LRUCache<>(CAPACITY);
        for (int i = 0; i < CAPACITY; i++) cache.put(i, i); // pre-fill

        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int n = i;
            pool.submit(() -> {
                try {
                    startGate.await();
                    cache.get(n % CAPACITY);
                    cache.put(n, n);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        done.await();
        pool.shutdown();

        assertTrue(cache.size() <= CAPACITY);
        assertEquals(CAPACITY, cache.size());
    }
}
