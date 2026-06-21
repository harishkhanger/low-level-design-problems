package lld.pubsub.cluster;

import lld.pubsub.Partition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerNode {

    private final int id;
    private volatile boolean alive = true;

    private final Map<Integer, Partition> replicas = new ConcurrentHashMap<>();

    public BrokerNode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return alive;
    }

    public void crash() {
        this.alive = false;
    }

    public void hostReplica(int partitionId) {
        replicas.put(partitionId, new Partition(partitionId));
    }

    public Partition replica(int partitionId) {
        return replicas.get(partitionId);
    }

    public boolean hostsReplica(int partitionId) {
        return replicas.containsKey(partitionId);
    }
}
