package lld.pubsub;

import java.util.List;

class Consumer implements Runnable {

    private static final long POLL_INTERVAL_MS = 5;

    private final ConsumerGroup group;
    private final List<Partition> assignedPartitions;
    private volatile boolean running = true;

    Consumer(ConsumerGroup group, List<Partition> assignedPartitions) {
        this.group = group;
        this.assignedPartitions = assignedPartitions;
    }

    @Override
    public void run() {
        while (running) {
            boolean deliveredSomething = false;

            for (Partition partition : assignedPartitions) {
                long offset = group.getOffset(partition.getId());
                List<Message> batch = partition.read(offset);
                for (Message message : batch) {
                    group.deliver(partition.getId(), offset, message);
                    offset++;
                }
                if (!batch.isEmpty()) {
                    group.commitOffset(partition.getId(), offset);
                    deliveredSomething = true;
                }
            }

            if (!deliveredSomething) {
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    void stop() {
        running = false;
    }
}
