package lld.notification.service;

import lld.notification.channel.Channel;
import lld.notification.model.Notification;
import lld.notification.model.SendResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class NotificationService {
    private final Map<String, List<Channel>> subscriptions = new ConcurrentHashMap<>();

    public void subscribe(String user, Channel channel) {
        subscriptions
            .computeIfAbsent(user, u -> new CopyOnWriteArrayList<>())
            .add(channel);
    }

    public void unsubscribe(String user, Channel channel) {
        List<Channel> channels = subscriptions.get(user);
        if (channels != null) {
            channels.remove(channel);
        }
    }

    public List<SendResult> notify(String user, String content) {
        List<Channel> channels = subscriptions.getOrDefault(user, List.of());
        Notification notification = new Notification(user, content);
        List<SendResult> results = new ArrayList<>();
        for (Channel channel : channels) {
            results.add(channel.send(notification));
        }
        return results;
    }
}
