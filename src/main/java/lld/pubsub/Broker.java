package lld.pubsub;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Broker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public void createTopic(String name, int partitionCount){
        Topic topic = new Topic(name, partitionCount);
        topics.put(name, topic);
    }

    public Topic getTopic(String name){
        Topic topic = topics.get(name);
        if (topic == null){
            throw new IllegalArgumentException("Invalid topic name: " + name);
        }
        return topic;
    }

    public Set<String> listTopics(){
        return topics.keySet();
    }
}
