package lld.notification.decorator;

import lld.notification.channel.Channel;
import lld.notification.model.Notification;
import lld.notification.model.SendResult;


public class RetryDecorator extends ChannelDecorator {
    private final int maxAttempts;

    public RetryDecorator(Channel wrapped, int maxAttempts) {
        super(wrapped);
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        this.maxAttempts = maxAttempts;
    }

    @Override
    public SendResult send(Notification notification) {
        SendResult last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            last = wrapped.send(notification);
            if (last.isSuccess()) {
                return last;
            }
            System.out.println("[RETRY] attempt " + attempt + " failed: " + last.getDetail());
        }
        return last;
    }
}
