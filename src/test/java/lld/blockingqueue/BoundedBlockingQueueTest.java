package lld.blockingqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedBlockingQueueTest {

    @Test
    @DisplayName("FIFO order is preserved for a single thread")
    void preservesFifoOrder() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(4);
        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertEquals(1, queue.take());
        assertEquals(2, queue.take());
        assertEquals(3, queue.take());
    }

    @Test
    @DisplayName("Rejects non-positive capacity")
    void rejectsInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new BoundedBlockingQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new BoundedBlockingQueue<>(-1));
    }

    @Test
    @DisplayName("put blocks while the queue is full and unblocks after a take")
    void putBlocksWhenFull() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        queue.put(1);

        AtomicInteger completed = new AtomicInteger(0);
        Thread producer = new Thread(() -> {
            try {
                queue.put(2);
                completed.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        Thread.sleep(100);
        assertEquals(0, completed.get(), "producer must still be blocked on a full queue");

        assertEquals(1, queue.take());
        producer.join(1000);
        assertEquals(1, completed.get(), "producer should proceed once a slot frees up");
    }

    @Test
    @DisplayName("offer returns false and enqueues nothing when the queue stays full")
    void offerTimesOutWhenFull() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        assertTrue(queue.offer(1, 50, TimeUnit.MILLISECONDS));
        assertFalse(queue.offer(2, 50, TimeUnit.MILLISECONDS), "second offer must time out");
        assertEquals(1, queue.size(), "the timed-out item must not be enqueued");
    }

    @Test
    @DisplayName("poll returns null when nothing arrives in time")
    void pollTimesOutWhenEmpty() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        assertNull(queue.poll(50, TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("offer succeeds once a slot frees up within the timeout")
    void offerSucceedsAfterSlotFrees() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        queue.put(1);

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(50);
                queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        assertTrue(queue.offer(2, 1, TimeUnit.SECONDS), "offer should succeed after the take frees a slot");
        consumer.join(1000);
    }

    @Test
    @DisplayName("Concurrent producers and consumers lose and duplicate nothing")
    void concurrentProducersAndConsumers() throws InterruptedException {
        final int producers = 8;
        final int consumers = 8;
        final int perProducer = 5_000;
        final int total = producers * perProducer;

        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(64);
        Set<Integer> consumed = ConcurrentHashMap.newKeySet();

        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch producersDone = new CountDownLatch(producers);
        CountDownLatch consumersDone = new CountDownLatch(consumers);
        AtomicInteger nextValue = new AtomicInteger(0);
        AtomicInteger takenCount = new AtomicInteger(0);

        for (int p = 0; p < producers; p++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    for (int i = 0; i < perProducer; i++) {
                        queue.put(nextValue.getAndIncrement());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    producersDone.countDown();
                }
            });
        }

        for (int c = 0; c < consumers; c++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    while (takenCount.get() < total) {
                        if (takenCount.incrementAndGet() > total) {
                            break;
                        }
                        consumed.add(queue.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    consumersDone.countDown();
                }
            });
        }

        startGate.countDown();
        producersDone.await();
        consumersDone.await();
        pool.shutdown();

        assertEquals(total, consumed.size(), "every produced item should be consumed exactly once");
        assertTrue(consumed.contains(0));
        assertTrue(consumed.contains(total - 1));
        assertEquals(0, queue.size(), "queue should be fully drained");
    }
}
