package lld.ratelimiter;

import lld.ratelimiter.strategy.RedisTokenBucketStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class RedisTokenBucketStrategyTest {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    private JedisPool pool;
    private MutableClock clock;

    @BeforeEach
    void setUp() {
        assumeTrue(redisReachable(), "Redis not reachable at localhost:6379 — skipping");
        pool = new JedisPool(HOST, PORT);
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll(); // clean slate between tests
        }
    }

    @AfterEach
    void tearDown() {
        if (pool != null) pool.close();
    }

    private static boolean redisReachable() {
        try (JedisPool p = new JedisPool(HOST, PORT); Jedis j = p.getResource()) {
            return "PONG".equalsIgnoreCase(j.ping());
        } catch (Exception e) {
            return false;
        }
    }

    private RateLimiter limiter() {
        return new RateLimiter(new RedisTokenBucketStrategy(pool, 5, 1.0, clock));
    }

    @Test
    @DisplayName("A fresh user can burst up to capacity, then is rejected")
    void burstUpToCapacity() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("alice"), "request " + i + " should be allowed");
        }
        assertFalse(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("Tokens refill over time")
    void refillsOverTime() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice"); // drain
        assertFalse(limiter.allowRequest("alice"));

        clock.advance(Duration.ofSeconds(3)); // 3 tokens back

        assertTrue(limiter.allowRequest("alice"));
        assertTrue(limiter.allowRequest("alice"));
        assertTrue(limiter.allowRequest("alice"));
        assertFalse(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("Each user has an independent bucket")
    void perUserIsolation() {
        RateLimiter limiter = limiter();
        for (int i = 0; i < 5; i++) limiter.allowRequest("alice");
        assertFalse(limiter.allowRequest("alice"));

        assertTrue(limiter.allowRequest("bob"));
    }

    @Test
    @DisplayName("Recovers when Redis drops the cached script (EVALSHA falls back to EVAL)")
    void survivesScriptCacheFlush() {
        RateLimiter limiter = limiter();
        assertTrue(limiter.allowRequest("alice")); // loads the script into Redis

        try (Jedis jedis = pool.getResource()) {
            jedis.scriptFlush(); // simulate a Redis restart: the script cache is wiped
        }

        // The next call's EVALSHA hits NOSCRIPT and falls back to EVAL — still works.
        assertTrue(limiter.allowRequest("alice"));
    }

    @Test
    @DisplayName("Two instances (servers) sharing Redis enforce ONE global limit")
    void oneGlobalLimitAcrossInstances() {
        // Two strategy instances on the same Redis = two app servers behind a load balancer.
        RateLimiter serverA = limiter();
        RateLimiter serverB = limiter();

        // Six requests for the same user, load-balanced across the two servers.
        RateLimiter[] roundRobin = {serverA, serverB, serverA, serverB, serverA, serverB};
        int allowed = 0;
        for (RateLimiter server : roundRobin) {
            if (server.allowRequest("alice")) allowed++;
        }

        assertEquals(5, allowed);
    }
}
