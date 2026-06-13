package lld.ratelimiter.strategy;

import java.time.Duration;


public record Rule(int maxRequests, Duration window) {
    public Rule {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be positive");
        }
    }
}
