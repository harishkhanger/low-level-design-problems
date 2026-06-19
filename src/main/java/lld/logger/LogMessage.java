package lld.logger;

import java.time.Instant;

public record LogMessage(LogLevel logLevel, String message, Instant timestamp) {
}
