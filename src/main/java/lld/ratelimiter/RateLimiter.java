package lld.ratelimiter;

import lld.ratelimiter.strategy.RateLimitStrategy;

public class RateLimiter {
    private final RateLimitStrategy rateLimitStrategy;

    public RateLimiter (RateLimitStrategy rateLimitStrategy){
        this.rateLimitStrategy = rateLimitStrategy;
    }

    public boolean allowRequest(String userId){
        return rateLimitStrategy.isValid(userId);
    }
}
