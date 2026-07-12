package lld.blockingqueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<T> {

    private final Deque<T> deque = new ArrayDeque<>();
    private final Semaphore emptySlots;
    private final Semaphore filledSlots;
    private final ReentrantLock lock = new ReentrantLock();

    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.emptySlots = new Semaphore(capacity);
        this.filledSlots = new Semaphore(0);
    }

    public void put(T item) throws InterruptedException {
        emptySlots.acquire();
        lock.lock();
        try {
            deque.addLast(item);
        } finally {
            lock.unlock();
        }
        filledSlots.release();
    }

    public T take() throws InterruptedException {
        filledSlots.acquire();
        lock.lock();
        T item;
        try {
            item = deque.pollFirst();
        } finally {
            lock.unlock();
        }
        emptySlots.release();
        return item;
    }

    public int size() {
        lock.lock();
        try {
            return deque.size();
        } finally {
            lock.unlock();
        }
    }
}
