package lld.logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

    @Test
    @DisplayName("Messages below the threshold are dropped")
    void dropsBelowThreshold() {
        CapturingAppender appender = new CapturingAppender();
        Logger logger = new Logger(LogLevel.WARN, List.of(appender));

        logger.debug("d");   // below WARN — dropped
        logger.info("i");    // below WARN — dropped
        logger.warn("w");    // kept
        logger.error("e");   // kept

        assertEquals(2, appender.captured.size());
        assertEquals(LogLevel.WARN, appender.captured.get(0).logLevel());
        assertEquals(LogLevel.ERROR, appender.captured.get(1).logLevel());
    }

    @Test
    @DisplayName("Messages at or above the threshold pass through")
    void keepsAtOrAboveThreshold() {
        CapturingAppender appender = new CapturingAppender();
        Logger logger = new Logger(LogLevel.INFO, List.of(appender));

        logger.debug("d");   // dropped
        logger.info("i");    // kept (exactly at threshold)
        logger.warn("w");    // kept
        logger.error("e");   // kept

        assertEquals(3, appender.captured.size());
    }

    @Test
    @DisplayName("One log call fans out to every appender")
    void fansOutToAllAppenders() {
        CapturingAppender console = new CapturingAppender();
        CapturingAppender file = new CapturingAppender();
        Logger logger = new Logger(LogLevel.DEBUG, List.of(console, file));

        logger.error("boom");

        assertEquals(1, console.captured.size());
        assertEquals(1, file.captured.size());
    }

    @Test
    @DisplayName("The captured message carries the right level and text")
    void capturesLevelAndMessage() {
        CapturingAppender appender = new CapturingAppender();
        Logger logger = new Logger(LogLevel.DEBUG, List.of(appender));

        logger.error("payment failed");

        LogMessage msg = appender.captured.get(0);
        assertEquals(LogLevel.ERROR, msg.logLevel());
        assertEquals("payment failed", msg.message());
    }

    @Test
    @DisplayName("A logger with no appenders simply does nothing")
    void noAppendersIsSafe() {
        Logger logger = new Logger(LogLevel.DEBUG, List.of());
        logger.error("nobody listening"); // must not throw
    }

    @Test
    @DisplayName("The SimpleFormatter renders level and message")
    void simpleFormatterRendersMessage() {
        var formatter = new lld.logger.formatter.SimpleFormatter();
        LogMessage msg = new LogMessage(LogLevel.INFO, "hello",
                java.time.Instant.parse("2026-01-01T00:00:00Z"));

        String rendered = formatter.format(msg);

        assertTrue(rendered.contains("INFO"));
        assertTrue(rendered.contains("hello"));
        assertTrue(rendered.contains("2026-01-01T00:00:00Z"));
    }
}
