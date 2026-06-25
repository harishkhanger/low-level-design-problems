package lld.notification.channel;

import lld.notification.model.Notification;
import lld.notification.model.SendResult;

public class EmailChannel implements Channel {
    @Override
    public SendResult send(Notification notification) {
        System.out.println("[EMAIL] to " + notification.getRecipient() + ": " + notification.getContent());
        return SendResult.ok("email sent");
    }
}
