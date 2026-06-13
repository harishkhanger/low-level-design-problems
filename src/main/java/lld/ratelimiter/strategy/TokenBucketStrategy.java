package lld.ratelimiter.strategy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketStrategy implements RateLimitStrategy {

    private final int capacity;
    private final double refillPerSecond;
    private final Clock clock;
    private final Map<String, Bucket> map = new ConcurrentHashMap<>();

    public TokenBucketStrategy(int capacity, double refillPerSecond, Clock clock) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.clock = clock;
    }

    @Override
    public boolean isValid(String userId) {
        Bucket bucket = map.computeIfAbsent(userId, k -> new Bucket(capacity, clock.instant()));

        synchronized (bucket) {
            Instant now = clock.instant();
            double elapsedSeconds = Duration.between(bucket.lastRefill, now).toMillis() / 1000.0;
            bucket.tokens = Math.min(capacity, bucket.tokens + elapsedSeconds * refillPerSecond);
            bucket.lastRefill = now;

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                return true;
            }
            return false;
        }
    }

    private static class Bucket {
        double tokens;
        Instant lastRefill;

        Bucket(double tokens, Instant lastRefill) {
            this.tokens = tokens;
            this.lastRefill = lastRefill;
        }
    }
}
