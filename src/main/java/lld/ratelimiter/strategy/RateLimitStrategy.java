package lld.ratelimiter.strategy;

public interface RateLimitStrategy {
    boolean isValid(String userId);
}
