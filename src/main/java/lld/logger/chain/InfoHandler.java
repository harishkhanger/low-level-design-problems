package lld.logger.chain;

import lld.logger.LogLevel;
import lld.logger.LogMessage;
import lld.logger.appender.LogAppender;
import java.time.Instant;

public class InfoHandler extends LogHandler {

    private final LogAppender logAppender;

    public InfoHandler(LogAppender logAppender) {
        super(LogLevel.INFO);
        this.logAppender = logAppender;
    }

    @Override
    protected void write(String message) {
        logAppender.append(new LogMessage(LogLevel.INFO, message, Instant.now()));
    }
}
