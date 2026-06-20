package lld.pubsub;

@FunctionalInterface
public interface MessageHandler {
    void onMessage(int partition, long offset, Message message);
}
