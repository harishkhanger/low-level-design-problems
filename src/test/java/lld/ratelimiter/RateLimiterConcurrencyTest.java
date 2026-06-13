package lld.ratelimiter;

import lld.ratelimiter.strategy.RateLimitStrategy;
import lld.ratelimiter.strategy.Rule;
import lld.ratelimiter.strategy.SlidingWindowRuleStrategy;
import lld.ratelimiter.strategy.TokenBucketStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RateLimiterConcurrencyTest {

    private static final int CAPACITY = 100;
    private static final int THREADS = 1000;

    private final MutableClock clock =
            new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    private int countAllowed(RateLimitStrategy strategy) throws InterruptedException {
        RateLimiter limiter = new RateLimiter(strategy);
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);
        AtomicInteger allowed = new AtomicInteger();

        for (int i = 0; i < THREADS; i++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    if (limiter.allowRequest("alice")) {
                        allowed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        done.await();
        pool.shutdown();
        return allowed.get();
    }

    @Test
    @DisplayName("Token bucket allows exactly capacity requests under heavy concurrency")
    void tokenBucketIsThreadSafe() throws InterruptedException {
        int allowed = countAllowed(new TokenBucketStrategy(CAPACITY, 0.0, clock));
        assertEquals(CAPACITY, allowed);
    }

    @Test
    @DisplayName("Sliding-window allows exactly the rule's max under heavy concurrency")
    void slidingWindowIsThreadSafe() throws InterruptedException {
        RateLimitStrategy strategy = new SlidingWindowRuleStrategy(
                List.of(new Rule(CAPACITY, Duration.ofSeconds(60))), clock);
        int allowed = countAllowed(strategy);
        assertEquals(CAPACITY, allowed);
    }
}
