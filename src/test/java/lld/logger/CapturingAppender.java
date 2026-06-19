package lld.logger;

import lld.logger.appender.LogAppender;

import java.util.ArrayList;
import java.util.List;

/**
 * A test-only appender that records every message it receives instead of writing
 * it anywhere. Lets tests assert what got through the logger (level filtering,
 * fan-out) without scraping console output.
 */
class CapturingAppender implements LogAppender {
    final List<LogMessage> captured = new ArrayList<>();

    @Override
    public void append(LogMessage message) {
        captured.add(message);
    }
}
