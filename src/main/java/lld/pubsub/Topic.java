package lld.pubsub;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private final String name;
    private final List<Partition> partitions;

    public Topic(String name, int partitionCount) {
        this.name = name;
        this.partitions = new ArrayList<>();
        int curr = 0;
        while (curr < partitionCount) {
            partitions.add(new Partition(curr++));
        }
    }

    public Partition partitionForKey(String key) {
        int index = Math.floorMod(key.hashCode(), partitions.size());
        return partitions.get(index);
    }

    public List<Partition> getPartitions() {
        return List.copyOf(partitions);
    }

    public String getName() {
        return name;
    }

    public int getPartitionCount() {
        return partitions.size();
    }
}
