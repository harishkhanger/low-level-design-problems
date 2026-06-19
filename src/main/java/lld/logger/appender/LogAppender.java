package lld.logger.appender;

import lld.logger.LogMessage;

public interface LogAppender {
    void append (LogMessage logMessage);
}
