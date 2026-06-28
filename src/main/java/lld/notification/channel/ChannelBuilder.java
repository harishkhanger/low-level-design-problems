package lld.notification.channel;

import lld.notification.decorator.RateLimitDecorator;
import lld.notification.decorator.RetryDecorator;

import java.time.Clock;
import java.time.Duration;

public class ChannelBuilder {
    private Channel channel;

    private ChannelBuilder(Channel base) {
        this.channel = base;
    }

    public static ChannelBuilder of(Channel base) {
        if (base == null) {
            throw new IllegalArgumentException("base channel must not be null");
        }
        return new ChannelBuilder(base);
    }

    public ChannelBuilder withRetry(int maxAttempts) {
        this.channel = new RetryDecorator(channel, maxAttempts);
        return this;
    }

    public ChannelBuilder withRateLimit(int maxInWindow, Duration window, Clock clock) {
        this.channel = new RateLimitDecorator(channel, maxInWindow, window, clock);
        return this;
    }

    public Channel build() {
        return channel;
    }
}
