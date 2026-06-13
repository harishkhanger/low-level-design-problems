package lld.lrucache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LRUCacheTest {

    @Test
    @DisplayName("put then get returns the stored value")
    void basicPutGet() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        assertEquals("A", cache.get(1));
    }

    @Test
    @DisplayName("get on a missing key returns null")
    void missingKeyReturnsNull() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        assertNull(cache.get(42));
    }

    @Test
    @DisplayName("put on an existing key updates its value")
    void updateExistingKey() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        cache.put(1, "B");
        assertEquals("B", cache.get(1));
    }

    @Test
    @DisplayName("inserting beyond capacity evicts the least-recently-used key")
    void evictsLeastRecentlyUsed() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");

        assertNull(cache.get(1));
        assertEquals("B", cache.get(2));
        assertEquals("C", cache.get(3));
    }

    @Test
    @DisplayName("a get refreshes recency, protecting that key from eviction")
    void getRefreshesRecency() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.get(1);
        cache.put(3, "C");

        assertEquals("A", cache.get(1));
        assertNull(cache.get(2));
        assertEquals("C", cache.get(3));
    }

    @Test
    @DisplayName("updating an existing key also refreshes its recency")
    void putRefreshesRecency() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(1, "A2");
        cache.put(3, "C");

        assertEquals("A2", cache.get(1));
        assertNull(cache.get(2));
        assertEquals("C", cache.get(3));
    }

    @Test
    @DisplayName("capacity of 1 evicts on every new key")
    void capacityOne() {
        LRUCache<Integer, String> cache = new LRUCache<>(1);
        cache.put(1, "A");
        cache.put(2, "B");

        assertNull(cache.get(1));
        assertEquals("B", cache.get(2));
    }
}
