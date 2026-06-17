package lld.ratelimiter.strategy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisNoScriptException;

import java.time.Clock;
import java.util.List;

public class RedisTokenBucketStrategy implements RateLimitStrategy {

    // KEYS[1] = bucket key
    // ARGV[1] = capacity, ARGV[2] = refillPerSecond, ARGV[3] = nowMillis, ARGV[4] = ttlSeconds
    private static final String LUA_SCRIPT = """
            local key      = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refill   = tonumber(ARGV[2])
            local now      = tonumber(ARGV[3])
            local ttl      = tonumber(ARGV[4])

            local data       = redis.call('HMGET', key, 'tokens', 'lastRefill')
            local tokens     = tonumber(data[1])
            local lastRefill = tonumber(data[2])

            -- First time we see this user: start with a full bucket.
            if tokens == nil then
                tokens = capacity
                lastRefill = now
            end

            -- Refill based on elapsed time, capped at capacity.
            local elapsedSeconds = (now - lastRefill) / 1000.0
            tokens = math.min(capacity, tokens + elapsedSeconds * refill)
            lastRefill = now

            local allowed = 0
            if tokens >= 1 then
                tokens = tokens - 1
                allowed = 1
            end

            redis.call('HSET', key, 'tokens', tokens, 'lastRefill', lastRefill)
            redis.call('EXPIRE', key, ttl)   -- let idle buckets clean themselves up
            return allowed
            """;

    private final JedisPool jedisPool;
    private final int capacity;
    private final double refillPerSecond;
    private final Clock clock;
    private final int ttlSeconds;


    private volatile String scriptSha;

    public RedisTokenBucketStrategy(JedisPool jedisPool, int capacity, double refillPerSecond, Clock clock) {
        this.jedisPool = jedisPool;
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.clock = clock;
        this.ttlSeconds = refillPerSecond > 0
                ? (int) Math.ceil(capacity / refillPerSecond) + 1
                : 3600;
    }

    @Override
    public boolean isValid(String userId) {
        List<String> keys = List.of("rate_limit:" + userId);
        List<String> args = List.of(
                String.valueOf(capacity),
                String.valueOf(refillPerSecond),
                String.valueOf(clock.instant().toEpochMilli()),
                String.valueOf(ttlSeconds));

        try (Jedis jedis = jedisPool.getResource()) {
            // Load the script once and remember its hash.
            if (scriptSha == null) {
                scriptSha = jedis.scriptLoad(LUA_SCRIPT);
            }
            Object result;
            try {
                result = jedis.evalsha(scriptSha, keys, args);
            } catch (JedisNoScriptException e) {
                result = jedis.eval(LUA_SCRIPT, keys, args);
            }
            return ((Long) result) == 1L;
        }
    }
}
