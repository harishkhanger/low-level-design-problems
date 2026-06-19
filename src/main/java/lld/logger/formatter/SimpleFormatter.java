package lld.logger.formatter;

import lld.logger.LogMessage;

public class SimpleFormatter implements LogFormatter{
    @Override
    public String format(LogMessage logMessage) {
        return logMessage.timestamp() + " [" + logMessage.logLevel() + "] " + logMessage.message();
    }
}
