package lld.ratelimiter;

import lld.ratelimiter.strategy.Rule;
import lld.ratelimiter.strategy.SlidingWindowRuleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingWindowRuleStrategyTest {

    private MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    }

    private RateLimiter multiRuleLimiter() {
        List<Rule> rules = List.of(
                new Rule(1, Duration.ofSeconds(1)),
                new Rule(3, Duration.ofSeconds(5)),
                new Rule(10, Duration.ofSeconds(60)));
        return new RateLimiter(new SlidingWindowRuleStrategy(rules, clock));
    }

    @Test
    @DisplayName("The 1-per-second rule blocks a rapid second request, then allows one a second later")
    void onePerSecond() {
        RateLimiter limiter = multiRuleLimiter();

        assertTrue(limiter.allowRequest("alice"));
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofSeconds(1));
        assertTrue(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("The 3-per-5-seconds rule blocks the 4th request even when spaced 1s apart")
    void threePerFiveSeconds() {
        RateLimiter limiter = multiRuleLimiter();

        assertTrue(limiter.allowRequest("alice"));   // t=0
        clock.advance(Duration.ofSeconds(1));
        assertTrue(limiter.allowRequest("alice"));   // t=1
        clock.advance(Duration.ofSeconds(1));
        assertTrue(limiter.allowRequest("alice"));   // t=2

        clock.advance(Duration.ofSeconds(1));
        assertFalse(limiter.allowRequest("alice"));  // blocked by 3/5s

        clock.advance(Duration.ofSeconds(2));
        assertTrue(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("Each user is limited independently")
    void perUserIsolation() {
        RateLimiter limiter = multiRuleLimiter();

        assertTrue(limiter.allowRequest("alice"));
        assertFalse(limiter.allowRequest("alice"));   // alice hits the 1/sec rule

        assertTrue(limiter.allowRequest("bob"));       // bob is unaffected
    }

    @Test
    @DisplayName("A standalone 10-per-minute rule allows 10 then blocks, and resets after the window")
    void tenPerMinuteInIsolation() {
        RateLimiter limiter = new RateLimiter(new SlidingWindowRuleStrategy(
                List.of(new Rule(10, Duration.ofSeconds(60))), clock));

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.allowRequest("alice"), "request " + i + " should be allowed");
        }
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofSeconds(60));
        assertTrue(limiter.allowRequest("alice"));    // allowed again
    }
}
