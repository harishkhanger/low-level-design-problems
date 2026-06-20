package lld.pubsub;

public class Producer {
    private final Broker broker;

    public Producer (Broker broker){
        this.broker = broker;
    }

    public long send(String topicName, String key, String value){
        Topic topic = broker.getTopic(topicName);
        Partition partition = topic.partitionForKey(key);
        return partition.append(new Message(key, value));
    }
}
