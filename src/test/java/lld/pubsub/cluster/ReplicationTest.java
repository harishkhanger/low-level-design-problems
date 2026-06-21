package lld.pubsub.cluster;

import lld.pubsub.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicationTest {

    @Test
    @DisplayName("Every replica of a partition stays byte-for-byte in sync with its leader")
    void replicasStayInSync() {
        Cluster cluster = new Cluster(3, 3, 3); // 3 brokers, 3 partitions, replication factor 3
        for (int i = 0; i < 30; i++) {
            cluster.publish("key" + i, "val" + i);
        }

        for (int p = 0; p < cluster.getNumPartitions(); p++) {
            List<Message> leaderLog = cluster.broker(cluster.leaderFor(p)).replica(p).read(0);
            for (int brokerId : cluster.replicasFor(p)) {
                List<Message> replicaLog = cluster.broker(brokerId).replica(p).read(0);
                assertEquals(leaderLog, replicaLog,
                        "replica on broker " + brokerId + " is out of sync for partition " + p);
            }
        }
    }

    @Test
    @DisplayName("Each partition has the configured number of replicas, with a leader among them")
    void replicaPlacement() {
        Cluster cluster = new Cluster(3, 3, 3);
        for (int p = 0; p < cluster.getNumPartitions(); p++) {
            List<Integer> replicas = cluster.replicasFor(p);
            assertEquals(3, replicas.size());
            assertTrue(replicas.contains(cluster.leaderFor(p)), "leader must be one of the replicas");
        }
    }

    @Test
    @DisplayName("When the leader's broker crashes, a follower is promoted with no data loss")
    void failoverPromotesFollowerWithoutDataLoss() {
        Cluster cluster = new Cluster(3, 3, 3);
        for (int i = 0; i < 30; i++) {
            cluster.publish("key" + i, "val" + i);
        }

        int partition = 0;
        int oldLeader = cluster.leaderFor(partition);
        List<Message> before = cluster.read(partition, 0);

        cluster.crashBroker(oldLeader);

        int newLeader = cluster.leaderFor(partition);
        assertNotEquals(oldLeader, newLeader, "a surviving follower should have been promoted");

        // The new leader was a follower that mirrored every write, so all data survives.
        List<Message> after = cluster.read(partition, 0);
        assertEquals(before, after, "no data should be lost on failover");
    }

    @Test
    @DisplayName("Writes continue to work after a failover, routed to the new leader")
    void writesContinueAfterFailover() {
        Cluster cluster = new Cluster(3, 3, 3);
        cluster.publish("a", "1");

        int partition = cluster.partitionFor("a");
        cluster.crashBroker(cluster.leaderFor(partition));

        cluster.publish("a", "2");

        List<Message> log = cluster.read(partition, 0);
        assertEquals(2, log.size());
        assertEquals("2", log.get(1).value());
    }
}
