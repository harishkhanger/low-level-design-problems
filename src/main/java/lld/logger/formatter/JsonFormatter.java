package lld.logger.formatter;

import lld.logger.LogMessage;

public class JsonFormatter implements LogFormatter{
    @Override
    public String format(LogMessage logMessage) {
        return String.format(
                """
                   {
                        "timestamp": "%s",
                        "level": "%s",
                        "message": "%s"
                   }
                """,
                logMessage.timestamp(), logMessage.logLevel(), logMessage.message()
        );
    }
}
