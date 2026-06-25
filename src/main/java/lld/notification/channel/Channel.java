package lld.notification.channel;

import lld.notification.model.Notification;
import lld.notification.model.SendResult;


public interface Channel {
    SendResult send(Notification notification);
}
