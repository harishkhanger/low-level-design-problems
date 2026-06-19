package lld.logger.formatter;

import lld.logger.LogMessage;

public interface LogFormatter {
    String format (LogMessage logMessage);
}
