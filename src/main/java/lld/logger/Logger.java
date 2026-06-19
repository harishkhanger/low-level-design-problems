package lld.logger;

import lld.logger.appender.LogAppender;

import java.time.Instant;
import java.util.List;

public class Logger {
    private final LogLevel threshold;
    private final List<LogAppender> logAppenders;

    public Logger(LogLevel threshold, List<LogAppender> logAppenders) {
        this.threshold = threshold;
        this.logAppenders = List.copyOf(logAppenders);
    }

    public void debug(String message){
        log(LogLevel.DEBUG, message);
    }

    public void info(String message){
        log(LogLevel.INFO, message);
    }

    public void warn(String message){
        log(LogLevel.WARN, message);
    }

    public void error(String message){
        log(LogLevel.ERROR, message);
    }

    private void log(LogLevel logLevel, String logMessage) {
        if (logLevel.compareTo(this.threshold) < 0) return;
        LogMessage message = new LogMessage(logLevel, logMessage, Instant.now());
        for (var appender : logAppenders) {
            appender.append(message);
        }
    }
}
