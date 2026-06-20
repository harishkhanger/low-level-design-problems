package lld.pubsub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class PubSubTest {

    /** Spin-wait (up to 2s) for an async condition to become true. */
    private void awaitTrue(BooleanSupplier condition) {
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) return;
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        fail("condition not met within timeout");
    }

    @Test
    @DisplayName("Fan-out: every consumer group independently receives all messages")
    void fanOutAcrossGroups() {
        Broker broker = new Broker();
        broker.createTopic("orders", 3);
        Topic topic = broker.getTopic("orders");
        Producer producer = new Producer(broker);

        int total = 30;
        for (int i = 0; i < total; i++) {
            producer.send("orders", "key" + (i % 5), "val" + i);
        }

        List<Message> billingGot = Collections.synchronizedList(new ArrayList<>());
        List<Message> analyticsGot = Collections.synchronizedList(new ArrayList<>());

        // billing has 2 consumers (load-balanced); analytics has 1. Both get everything.
        ConsumerGroup billing = new ConsumerGroup("billing", topic, 2,
                (p, o, m) -> billingGot.add(m));
        ConsumerGroup analytics = new ConsumerGroup("analytics", topic, 1,
                (p, o, m) -> analyticsGot.add(m));
        billing.start();
        analytics.start();

        awaitTrue(() -> billingGot.size() == total && analyticsGot.size() == total);

        billing.stop();
        analytics.stop();

        assertEquals(total, billingGot.size());   // billing saw every message
        assertEquals(total, analyticsGot.size()); // analytics saw every message too
    }

    @Test
    @DisplayName("Ordering: messages with the same key are delivered in publish order")
    void orderingWithinPartition() {
        Broker broker = new Broker();
        broker.createTopic("events", 3);
        Topic topic = broker.getTopic("events");
        Producer producer = new Producer(broker);

        // Same key -> same partition -> must be delivered in order.
        int total = 10;
        for (int i = 0; i < total; i++) {
            producer.send("events", "sameKey", "v" + i);
        }

        List<String> received = Collections.synchronizedList(new ArrayList<>());
        ConsumerGroup group = new ConsumerGroup("g", topic, 1,
                (p, o, m) -> received.add(m.value()));
        group.start();

        awaitTrue(() -> received.size() == total);
        group.stop();

        List<String> expected = new ArrayList<>();
        for (int i = 0; i < total; i++) expected.add("v" + i);
        assertEquals(expected, received); // v0, v1, ... v9 in order
    }
}
