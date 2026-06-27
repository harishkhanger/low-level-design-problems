package lld.notification.decorator;

import lld.notification.channel.Channel;
import lld.notification.model.Notification;
import lld.notification.model.SendResult;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;


public class RateLimitDecorator extends ChannelDecorator {
    private final int maxInWindow;
    private final Duration window;
    private final Clock clock;
    private final Deque<Long> timestamps = new ArrayDeque<>();

    public RateLimitDecorator(Channel wrapped, int maxInWindow, Duration window, Clock clock) {
        super(wrapped);
        this.maxInWindow = maxInWindow;
        this.window = window;
        this.clock = clock;
    }

    @Override
    public synchronized SendResult send(Notification notification) {
        long now = clock.millis();
        long cutoff = now - window.toMillis();
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.pollFirst();
        }
        if (timestamps.size() >= maxInWindow) {
            return SendResult.fail("rate limited");
        }
        timestamps.addLast(now);
        return wrapped.send(notification);
    }
}
