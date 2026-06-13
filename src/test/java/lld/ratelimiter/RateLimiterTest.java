package lld.ratelimiter;

import lld.ratelimiter.strategy.TokenBucketStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    private MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    }


    private RateLimiter limiter() {
        return new RateLimiter(new TokenBucketStrategy(5, 1.0, clock));
    }

    @Test
    @DisplayName("A fresh user can burst up to capacity, then is rejected")
    void burstUpToCapacity() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("alice"), "request " + i + " should be allowed");
        }
        assertFalse(limiter.allowRequest("alice"), "6th request should be rejected");
    }

    @Test
    @DisplayName("Tokens refill over time, restoring some capacity")
    void refillsOverTime() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice");
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofSeconds(3));   // 3 seconds * 1 token/s = 3 tokens

        assertTrue(limiter.allowRequest("alice"));
        assertTrue(limiter.allowRequest("alice"));
        assertTrue(limiter.allowRequest("alice"));
        assertFalse(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("Refill never exceeds capacity, even after a long idle period")
    void refillCappedAtCapacity() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice");

        clock.advance(Duration.ofSeconds(100));  // would be 100 tokens, but cap is 5

        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("alice"), "request " + i + " should be allowed");
        }
        assertFalse(limiter.allowRequest("alice"), "capacity is still 5, not 100");
    }

    @Test
    @DisplayName("Each user has an independent bucket")
    void perUserIsolation() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice");   // exhaust alice
        assertFalse(limiter.allowRequest("alice"));

        // Bob is untouched — his bucket is still full.
        assertTrue(limiter.allowRequest("bob"));
    }

    @Test
    @DisplayName("Partial refill: half a second at 1 token/s is not enough for a token")
    void partialRefillBelowOne() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice");
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofMillis(500));
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofMillis(500));
        assertTrue(limiter.allowRequest("alice"));
    }
}
