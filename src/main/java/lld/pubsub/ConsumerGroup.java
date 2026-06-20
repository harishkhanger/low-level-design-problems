package lld.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerGroup {

    private final String groupId;
    private final List<Consumer> consumers = new ArrayList<>();
    private final MessageHandler handler;

    private final Map<Integer, Long> offsets = new ConcurrentHashMap<>();
    private final ExecutorService pool;

    public ConsumerGroup(String groupId, Topic topic, int numConsumers, MessageHandler handler) {
        this.groupId = groupId;
        this.handler = handler;
        this.pool = Executors.newFixedThreadPool(numConsumers);

        List<Partition> partitions = topic.getPartitions();
        for (Partition p : partitions) {
            offsets.put(p.getId(), 0L);
        }

        List<List<Partition>> buckets = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            buckets.add(new ArrayList<>());
        }
        for (int i = 0; i < partitions.size(); i++) {
            buckets.get(i % numConsumers).add(partitions.get(i));
        }
        for (List<Partition> assigned : buckets) {
            consumers.add(new Consumer(this, assigned));
        }
    }

    public void start() {
        for (Consumer consumer : consumers) {
            pool.submit(consumer);
        }
    }

    public void stop() {
        consumers.forEach(Consumer::stop);
        pool.shutdownNow();
        try {
            pool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    long getOffset(int partitionId) {
        return offsets.getOrDefault(partitionId, 0L);
    }

    void commitOffset(int partitionId, long offset) {
        offsets.put(partitionId, offset);
    }

    void deliver(int partitionId, long offset, Message message) {
        handler.onMessage(partitionId, offset, message);
    }

    public String getGroupId() {
        return groupId;
    }
}
