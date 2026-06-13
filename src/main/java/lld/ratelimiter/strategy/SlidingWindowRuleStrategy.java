package lld.ratelimiter.strategy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SlidingWindowRuleStrategy implements RateLimitStrategy {

    private final List<Rule> rules;
    private final Duration maxWindow;
    private final Clock clock;
    private final Map<String, Deque<Instant>> requestLog = new ConcurrentHashMap<>();

    public SlidingWindowRuleStrategy(List<Rule> rules, Clock clock) {
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("At least one rule is required");
        }
        this.rules = List.copyOf(rules);
        this.clock = clock;
        this.maxWindow = rules.stream().map(Rule::window).max(Duration::compareTo).orElseThrow();
    }

    public boolean isValid(String userId) {
        Instant now = clock.instant();
        Deque<Instant> timeStamps = requestLog.computeIfAbsent(userId, k -> new ArrayDeque<>());

        synchronized (timeStamps) {
            Instant globalCutOff = now.minus(maxWindow);
            while (!timeStamps.isEmpty() && !timeStamps.peekFirst().isAfter(globalCutOff)) {
                timeStamps.pollFirst();
            }

            for (Rule rule : rules) {
                Instant windowStart = now.minus(rule.window());
                long count = 0;

                for (Iterator<Instant> iterator = timeStamps.descendingIterator(); iterator.hasNext(); ) {
                    if (!iterator.next().isAfter(windowStart)) {
                        break;
                    }
                    count++;
                }
                if (count >= rule.maxRequests()) {
                    return false;
                }
            }
            timeStamps.addLast(now);
            return true;
        }
    }
}
