package lld.pubsub.cluster;

import lld.pubsub.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cluster {

    private final List<BrokerNode> brokers = new ArrayList<>();
    private final int numPartitions;

    private final Map<Integer, Integer> leaderBrokerId = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> replicaBrokerIds = new ConcurrentHashMap<>();

    public Cluster(int numBrokers, int numPartitions, int replicationFactor) {
        if (replicationFactor > numBrokers) {
            throw new IllegalArgumentException("replicationFactor cannot exceed the number of brokers");
        }
        this.numPartitions = numPartitions;
        for (int i = 0; i < numBrokers; i++) {
            brokers.add(new BrokerNode(i));
        }

        for (int p = 0; p < numPartitions; p++) {
            List<Integer> replicas = new ArrayList<>();
            for (int r = 0; r < replicationFactor; r++) {
                int brokerId = (p + r) % numBrokers;
                replicas.add(brokerId);
                brokers.get(brokerId).hostReplica(p);
            }
            replicaBrokerIds.put(p, replicas);
            leaderBrokerId.put(p, replicas.getFirst());
        }
    }

    public int partitionFor(String key) {
        return Math.floorMod(key.hashCode(), numPartitions);
    }


    public synchronized long publish(String key, String value) {
        int partitionId = partitionFor(key);
        Message message = new Message(key, value);

        BrokerNode leader = brokers.get(leaderBrokerId.get(partitionId));
        long offset = leader.replica(partitionId).append(message);

        for (int brokerId : replicaBrokerIds.get(partitionId)) {
            if (brokerId == leader.getId()) {
                continue;
            }
            BrokerNode follower = brokers.get(brokerId);
            if (follower.isAlive()) {
                follower.replica(partitionId).append(message);
            }
        }
        return offset;
    }

    public List<Message> read(int partitionId, long fromOffset) {
        BrokerNode leader = brokers.get(leaderBrokerId.get(partitionId));
        return leader.replica(partitionId).read(fromOffset);
    }

    public synchronized void crashBroker(int brokerId) {
        brokers.get(brokerId).crash();
        for (int p = 0; p < numPartitions; p++) {
            if (leaderBrokerId.get(p) == brokerId) {
                for (int candidate : replicaBrokerIds.get(p)) {
                    if (candidate != brokerId && brokers.get(candidate).isAlive()) {
                        leaderBrokerId.put(p, candidate);
                        break;
                    }
                }
            }
        }
    }

    public int leaderFor(int partitionId) {
        return leaderBrokerId.get(partitionId);
    }

    public List<Integer> replicasFor(int partitionId) {
        return List.copyOf(replicaBrokerIds.get(partitionId));
    }

    public BrokerNode broker(int brokerId) {
        return brokers.get(brokerId);
    }

    public int getNumPartitions() {
        return numPartitions;
    }
}
