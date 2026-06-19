package lld.logger;

import lld.logger.chain.ErrorHandler;
import lld.logger.chain.InfoHandler;
import lld.logger.chain.LogHandler;
import lld.logger.chain.WarnHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Chain of Responsibility: info -> warn -> error. A message walks the chain and
 * is handled by the link whose level matches; everything else passes it along.
 */
class LogHandlerChainTest {

    private CapturingAppender infoOut;
    private CapturingAppender warnOut;
    private CapturingAppender errorOut;

    private LogHandler buildChain() {
        infoOut = new CapturingAppender();
        warnOut = new CapturingAppender();
        errorOut = new CapturingAppender();

        LogHandler info = new InfoHandler(infoOut);
        info.setNext(new WarnHandler(warnOut)).setNext(new ErrorHandler(errorOut));
        return info; // head of the chain
    }

    @Test
    @DisplayName("A WARN message passes Info and is handled by Warn only")
    void warnRoutedToWarnHandler() {
        LogHandler chain = buildChain();

        chain.handle(LogLevel.WARN, "low disk");

        assertEquals(0, infoOut.captured.size());
        assertEquals(1, warnOut.captured.size());
        assertEquals(0, errorOut.captured.size());
        assertEquals("low disk", warnOut.captured.get(0).message());
    }

    @Test
    @DisplayName("An ERROR message walks to the end and is handled by Error")
    void errorRoutedToErrorHandler() {
        LogHandler chain = buildChain();

        chain.handle(LogLevel.ERROR, "boom");

        assertEquals(0, infoOut.captured.size());
        assertEquals(0, warnOut.captured.size());
        assertEquals(1, errorOut.captured.size());
    }

    @Test
    @DisplayName("An INFO message is handled by the first link")
    void infoRoutedToInfoHandler() {
        LogHandler chain = buildChain();

        chain.handle(LogLevel.INFO, "started");

        assertEquals(1, infoOut.captured.size());
        assertEquals(0, warnOut.captured.size());
        assertEquals(0, errorOut.captured.size());
    }

    @Test
    @DisplayName("A level with no handler falls off the end of the chain")
    void unhandledLevelFallsOff() {
        LogHandler chain = buildChain();

        chain.handle(LogLevel.DEBUG, "verbose"); // no DEBUG handler in this chain

        assertEquals(0, infoOut.captured.size());
        assertEquals(0, warnOut.captured.size());
        assertEquals(0, errorOut.captured.size());
    }
}
