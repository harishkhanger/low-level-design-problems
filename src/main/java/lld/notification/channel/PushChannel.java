package lld.notification.channel;

import lld.notification.model.Notification;
import lld.notification.model.SendResult;

public class PushChannel implements Channel {
    @Override
    public SendResult send(Notification notification) {
        System.out.println("[PUSH] to " + notification.getRecipient() + ": " + notification.getContent());
        return SendResult.ok("push sent");
    }
}
