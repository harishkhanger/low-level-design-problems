package lld.notification.channel;

import lld.notification.model.Notification;
import lld.notification.model.SendResult;

public class SmsChannel implements Channel {
    @Override
    public SendResult send(Notification notification) {
        System.out.println("[SMS] to " + notification.getRecipient() + ": " + notification.getContent());
        return SendResult.ok("sms sent");
    }
}
