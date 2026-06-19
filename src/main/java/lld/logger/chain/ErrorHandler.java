package lld.logger.chain;

import lld.logger.LogLevel;
import lld.logger.LogMessage;
import lld.logger.appender.LogAppender;

import java.time.Instant;


public class ErrorHandler extends LogHandler {
    private final LogAppender logAppender;

    public ErrorHandler(LogAppender logAppender) {
        super(LogLevel.ERROR);
        this.logAppender = logAppender;
    }

    @Override
    protected void write(String message) {
        logAppender.append(new LogMessage(LogLevel.ERROR, message, Instant.now()));
    }
}
