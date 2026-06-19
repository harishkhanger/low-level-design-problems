package lld.logger.chain;

import lld.logger.LogLevel;

public abstract class LogHandler {
    private final LogLevel level;
    private LogHandler next;

    protected LogHandler (LogLevel logLevel){
        this.level = logLevel;
    }

    public LogHandler setNext (LogHandler next){
        this.next = next;
        return next;
    }

    public void handle(LogLevel messageLevel, String message){
        if (messageLevel == this.level){
            write(message);
        }else if (next!=null){
            next.handle(messageLevel, message);
        }
    }

    protected abstract void write(String message);
}
