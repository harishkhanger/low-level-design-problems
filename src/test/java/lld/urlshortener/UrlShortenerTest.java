package lld.urlshortener;

import lld.urlshortener.service.UrlShortener;
import lld.urlshortener.strategy.Base62Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlShortenerTest {

    private static final String BASE = "short.ly/";
    private UrlShortener service;

    @BeforeEach
    void setUp() {
        service = new UrlShortener(new Base62Generator(), BASE);
    }

    @Test
    @DisplayName("Shorten then expand returns the original URL")
    void roundTrip() {
        String shortUrl = service.shorten("https://example.com/some/long/path");
        assertTrue(shortUrl.startsWith(BASE));
        assertEquals("https://example.com/some/long/path", service.expand(shortUrl));
    }

    @Test
    @DisplayName("Shortening the same URL twice returns the same code")
    void dedupSameUrl() {
        String a = service.shorten("https://example.com/x");
        String b = service.shorten("https://example.com/x");
        assertEquals(a, b);
    }

    @Test
    @DisplayName("Different URLs get different codes")
    void distinctUrlsDistinctCodes() {
        assertTrue(!service.shorten("https://a.com").equals(service.shorten("https://b.com")));
    }

    @Test
    @DisplayName("A custom alias is used as the code and expands correctly")
    void customAlias() {
        String shortUrl = service.shortenWithAlias("https://example.com/y", "my-link");
        assertEquals(BASE + "my-link", shortUrl);
        assertEquals("https://example.com/y", service.expand(BASE + "my-link"));
    }

    @Test
    @DisplayName("Reusing a taken alias is rejected")
    void duplicateAliasRejected() {
        service.shortenWithAlias("https://example.com/y", "my-link");
        assertThrows(IllegalArgumentException.class,
                () -> service.shortenWithAlias("https://other.com/z", "my-link"));
    }

    @Test
    @DisplayName("Expanding an unknown short URL throws")
    void expandUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.expand(BASE + "nope"));
    }

    @Test
    @DisplayName("Concurrent shortening of the SAME url yields one shared code")
    void concurrentSameUrlOneCode() throws InterruptedException {
        Set<String> results = runConcurrently(200, i -> service.shorten("https://example.com/same"));
        assertEquals(1, results.size(), "all threads must get the same short URL");
    }

    @Test
    @DisplayName("Concurrent shortening of DISTINCT urls never collides")
    void concurrentDistinctUrlsNoCollision() throws InterruptedException {
        int n = 500;
        Set<String> results = runConcurrently(n, i -> service.shorten("https://example.com/" + i));
        assertEquals(n, results.size(), "every distinct URL must get a distinct code");
    }

    /** Run `action` on `count` threads released together; return the distinct results. */
    private Set<String> runConcurrently(int count, java.util.function.IntFunction<String> action)
            throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(count);
        Set<String> results = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < count; i++) {
            int idx = i;
            pool.submit(() -> {
                try {
                    startGate.await();
                    results.add(action.apply(idx));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        startGate.countDown();
        done.await();
        pool.shutdown();
        return results;
    }
}
