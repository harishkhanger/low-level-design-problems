package lld.notification;

import lld.notification.channel.Channel;
import lld.notification.channel.ChannelBuilder;
import lld.notification.model.Notification;
import lld.notification.model.SendResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelBuilderTest {

    static class FlakyChannel implements Channel {
        private final int failuresBeforeSuccess;
        private final AtomicInteger calls = new AtomicInteger(0);

        FlakyChannel(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        int callCount() {
            return calls.get();
        }

        @Override
        public SendResult send(Notification notification) {
            int n = calls.incrementAndGet();
            return n > failuresBeforeSuccess
                ? SendResult.ok("sent on attempt " + n)
                : SendResult.fail("transient error on attempt " + n);
        }
    }

    static class MutableClock extends Clock {
        private Instant now = Instant.EPOCH;

        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    @Test
    void builderAssemblesRetryThatRescuesTransientFailure() {
        FlakyChannel flaky = new FlakyChannel(1);           // fails once, then ok
        Channel channel = ChannelBuilder.of(flaky).withRetry(3).build();

        SendResult result = channel.send(new Notification("alice", "hi"));

        assertTrue(result.isSuccess());
        assertEquals(2, flaky.callCount());
    }

    @Test
    void rateLimitWrapsOutsideRetrySoOneLogicalSendCountsOnce() {
        FlakyChannel flaky = new FlakyChannel(1);           // fails once, then ok
        Channel channel = ChannelBuilder.of(flaky)
            .withRetry(3)
            .withRateLimit(1, Duration.ofSeconds(1), new MutableClock())
            .build();

        assertTrue(channel.send(new Notification("alice", "one")).isSuccess());
        assertFalse(channel.send(new Notification("alice", "two")).isSuccess());
    }
}
