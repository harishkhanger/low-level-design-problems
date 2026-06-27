package lld.notification;

import lld.notification.channel.Channel;
import lld.notification.channel.EmailChannel;
import lld.notification.decorator.RateLimitDecorator;
import lld.notification.decorator.RetryDecorator;
import lld.notification.model.Notification;
import lld.notification.model.SendResult;
import lld.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {

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

        void advance(Duration d) {
            now = now.plus(d);
        }

        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    @Test
    void retrySucceedsAfterTransientFailures() {
        FlakyChannel flaky = new FlakyChannel(2);          // fails twice, then ok
        Channel channel = new RetryDecorator(flaky, 3);

        SendResult result = channel.send(new Notification("alice", "hi"));

        assertTrue(result.isSuccess());
        assertEquals(3, flaky.callCount());                 // proved it retried
    }

    @Test
    void retryGivesUpAfterMaxAttempts() {
        FlakyChannel alwaysFails = new FlakyChannel(99);
        Channel channel = new RetryDecorator(alwaysFails, 3);

        SendResult result = channel.send(new Notification("alice", "hi"));

        assertFalse(result.isSuccess());
        assertEquals(3, alwaysFails.callCount());
    }

    @Test
    void rateLimitRejectsOverTheLimitWithinWindow() {
        MutableClock clock = new MutableClock();
        Channel channel = new RateLimitDecorator(new EmailChannel(), 2, Duration.ofSeconds(1), clock);
        Notification n = new Notification("bob", "ping");

        assertTrue(channel.send(n).isSuccess());
        assertTrue(channel.send(n).isSuccess());
        assertFalse(channel.send(n).isSuccess());

        clock.advance(Duration.ofSeconds(1));
        assertTrue(channel.send(n).isSuccess());
    }

    @Test
    void serviceFansOutToAllSubscribedChannels() {
        NotificationService service = new NotificationService();
        FlakyChannel email = new FlakyChannel(0);
        FlakyChannel sms = new FlakyChannel(0);
        service.subscribe("carol", email);
        service.subscribe("carol", sms);

        List<SendResult> results = service.notify("carol", "your order shipped");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(SendResult::isSuccess));
        assertEquals(1, email.callCount());
        assertEquals(1, sms.callCount());
    }

    @Test
    void decoratorsStackAndServiceTreatsThemAsAPlainChannel() {
        NotificationService service = new NotificationService();
        FlakyChannel flaky = new FlakyChannel(1);

        Channel stacked = new RateLimitDecorator(
            new RetryDecorator(flaky, 3),
            5, Duration.ofSeconds(1), new MutableClock());
        service.subscribe("dave", stacked);

        List<SendResult> results = service.notify("dave", "hello");

        assertEquals(1, results.size());
        assertTrue(results.getFirst().isSuccess());
        assertEquals(2, flaky.callCount());
    }
}
