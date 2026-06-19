package lld.logger.appender;

import lld.logger.LogMessage;
import lld.logger.formatter.LogFormatter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileAppender implements LogAppender{
    private final LogFormatter logFormatter;
    private final Path path;

    public FileAppender (LogFormatter logFormatter, Path path){
        this.logFormatter = logFormatter;
        this.path = path;
    }
    @Override
    public synchronized void append(LogMessage logMessage) {
        String line = logFormatter.format(logMessage) + System.lineSeparator();
        try {
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException ex){
            throw new UncheckedIOException(ex);
        }
    }
}
