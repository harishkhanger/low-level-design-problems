package lld.logger;

import lld.logger.appender.FileAppender;
import lld.logger.formatter.JsonFormatter;
import lld.logger.formatter.SimpleFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerExtensionsTest {

    @Test
    @DisplayName("JsonFormatter renders the message as JSON")
    void jsonFormatterProducesJson() {
        String out = new JsonFormatter().format(
                new LogMessage(LogLevel.WARN, "low mem", Instant.parse("2026-01-01T00:00:00Z")));

        assertTrue(out.contains("{") && out.contains("}"), out);
        assertTrue(out.contains("\"level\"") && out.contains("WARN"), out);
        assertTrue(out.contains("\"message\"") && out.contains("low mem"), out);
    }

    @Test
    @DisplayName("FileAppender writes the formatted line to disk")
    void fileAppenderWritesToFile(@TempDir Path dir) throws IOException {
        Path logFile = dir.resolve("app.log");
        FileAppender appender = new FileAppender(new SimpleFormatter(), logFile);

        appender.append(new LogMessage(LogLevel.ERROR, "disk full",
                Instant.parse("2026-01-01T00:00:00Z")));

        String content = Files.readString(logFile);
        assertTrue(content.contains("ERROR"), content);
        assertTrue(content.contains("disk full"), content);
    }

    @Test
    @DisplayName("Payoff: one logger writes plain text to console and JSON to a file, unchanged")
    void differentFormattersPerDestination(@TempDir Path dir) throws IOException {
        Path logFile = dir.resolve("app.log");
        // Console gets plain text; file gets JSON — same Logger, two formatters.
        Logger logger = new Logger(LogLevel.INFO, List.of(
                new FileAppender(new JsonFormatter(), logFile)));

        logger.error("boom");

        String content = Files.readString(logFile);
        assertTrue(content.contains("\"message\"") && content.contains("boom"), content); // file is JSON
    }
}
