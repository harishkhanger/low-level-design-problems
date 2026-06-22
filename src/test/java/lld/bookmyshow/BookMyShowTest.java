package lld.bookmyshow;

import lld.bookmyshow.model.*;
import lld.bookmyshow.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookMyShowTest {

    private static final Duration LOCK_TIMEOUT = Duration.ofMinutes(5);
    private static final Instant T0 = Instant.parse("2026-01-01T18:00:00Z");

    private Show show;
    private Seat a5;

    @BeforeEach
    void setUp() {
        a5 = new Seat("A5");
        show = new Show("S1", new Movie("M1", "Inception"), "NYC", "AMC",
                T0, List.of(a5, new Seat("A6")));
    }

    private SeatLockManager fixedClockManager() {
        return new SeatLockManager(Clock.fixed(T0, ZoneOffset.UTC), LOCK_TIMEOUT);
    }

    @Test
    @DisplayName("A successful payment confirms the booking")
    void bookingConfirmedOnPayment() {
        BookingService service = new BookingService(fixedClockManager(), user -> true);
        Booking booking = service.book(show, List.of(a5), new User("u1", "Alice"));
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("A declined payment expires the booking and frees the seat for others")
    void declinedPaymentReleasesSeat() {
        SeatLockManager manager = fixedClockManager();

        Booking declined = new BookingService(manager, user -> false)
                .book(show, List.of(a5), new User("u1", "Alice"));
        assertEquals(BookingStatus.EXPIRED, declined.getStatus());

        // The seat was released, so a paying user can now book it.
        Booking confirmed = new BookingService(manager, user -> true)
                .book(show, List.of(a5), new User("u2", "Bob"));
        assertEquals(BookingStatus.CONFIRMED, confirmed.getStatus());
    }

    @Test
    @DisplayName("Booking an already-booked seat is rejected")
    void cannotBookAlreadyBookedSeat() {
        BookingService service = new BookingService(fixedClockManager(), user -> true);
        service.book(show, List.of(a5), new User("u1", "Alice"));

        assertThrows(IllegalStateException.class,
                () -> service.book(show, List.of(a5), new User("u2", "Bob")));
    }

    @Test
    @DisplayName("Race: many threads fight for the same seat — exactly one wins")
    void concurrentRaceExactlyOneWins() throws InterruptedException {
        BookingService service = new BookingService(fixedClockManager(), user -> true);

        int threads = 100;
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger confirmed = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            User user = new User("u" + i, "user" + i);
            pool.submit(() -> {
                try {
                    startGate.await();
                    Booking booking = service.book(show, List.of(a5), user);
                    if (booking.getStatus() == BookingStatus.CONFIRMED) {
                        confirmed.incrementAndGet();
                    }
                } catch (IllegalStateException e) {
                    rejected.incrementAndGet(); // seat was taken
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

        assertEquals(1, confirmed.get(), "exactly one booking must win the seat");
        assertEquals(threads - 1, rejected.get(), "everyone else must be rejected");
    }

    @Test
    @DisplayName("An expired lock frees the seat for another user")
    void expiredLockFreesSeat() {
        MutableClock clock = new MutableClock(T0, ZoneOffset.UTC);
        SeatLockManager manager = new SeatLockManager(clock, LOCK_TIMEOUT);
        User alice = new User("u1", "Alice");
        User bob = new User("u2", "Bob");

        assertTrue(manager.lockSeats(List.of(a5), show, alice));   // Alice holds A5
        assertFalse(manager.lockSeats(List.of(a5), show, bob));    // Bob can't grab it

        clock.advance(Duration.ofMinutes(6));                      // Alice's lock expires

        assertTrue(manager.lockSeats(List.of(a5), show, bob));     // now Bob can
    }
}
