package lld.logger.chain;

import lld.logger.LogLevel;
import lld.logger.LogMessage;
import lld.logger.appender.LogAppender;
import java.time.Instant;

public class WarnHandler extends LogHandler{
    private final LogAppender logAppender;

    public WarnHandler(LogAppender logAppender) {
        super(LogLevel.WARN);
        this.logAppender = logAppender;
    }

    @Override
    protected void write(String message) {
        logAppender.append(new LogMessage(LogLevel.WARN, message, Instant.now()));
    }
}
