package lld.ridesharing;

import lld.ridesharing.model.Driver;
import lld.ridesharing.model.DriverStatus;
import lld.ridesharing.model.Location;
import lld.ridesharing.model.Rider;
import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;
import lld.ridesharing.service.RideService;
import lld.ridesharing.strategy.DistanceBasedPricing;
import lld.ridesharing.strategy.NearestDriverStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RideServiceTest {

    private RideService service;
    private final Rider rider = new Rider("r1", "Rita");

    @BeforeEach
    void setUp() {
        // base 5.0 + 2.0 per unit distance
        service = new RideService(new NearestDriverStrategy(), new DistanceBasedPricing(5.0, 2.0));
    }

    private Driver availableDriver(String id, double x, double y) {
        return new Driver(id, id, new Location(x, y), DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Happy path: request -> start -> end yields fare and frees the driver")
    void fullTripLifecycle() {
        Driver driver = availableDriver("d1", 0, 0);
        service.registerDriver(driver);

        Trip trip = service.requestTrip(rider, new Location(0, 0), new Location(3, 4));
        assertEquals(TripStatus.ASSIGNED, trip.getStatus());
        assertEquals(DriverStatus.ON_TRIP, driver.getDriverStatus()); // claimed

        trip.start();
        assertEquals(TripStatus.IN_PROGRESS, trip.getStatus());

        trip.end();
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertEquals(15.0, trip.getFare(), 0.001);                    // 5 + 2 * distance(=5)
        assertEquals(DriverStatus.AVAILABLE, driver.getDriverStatus()); // freed again
    }

    @Test
    @DisplayName("Matching picks the nearest available driver")
    void picksNearestDriver() {
        service.registerDriver(availableDriver("far", 10, 10));
        service.registerDriver(availableDriver("near", 1, 1));

        Trip trip = service.requestTrip(rider, new Location(0, 0), new Location(2, 2));
        assertEquals("near", trip.getDriver().getId());
    }

    @Test
    @DisplayName("Ending a trip before it starts is rejected")
    void cannotEndBeforeStart() {
        service.registerDriver(availableDriver("d1", 0, 0));
        Trip trip = service.requestTrip(rider, new Location(0, 0), new Location(1, 1));
        assertThrows(IllegalStateException.class, trip::end);
    }

    @Test
    @DisplayName("Cancelling frees the driver")
    void cancelFreesDriver() {
        Driver driver = availableDriver("d1", 0, 0);
        service.registerDriver(driver);
        Trip trip = service.requestTrip(rider, new Location(0, 0), new Location(1, 1));

        trip.cancel();
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals(DriverStatus.AVAILABLE, driver.getDriverStatus());
    }

    @Test
    @DisplayName("Requesting with no available drivers is rejected")
    void noDriversThrows() {
        assertThrows(IllegalStateException.class,
                () -> service.requestTrip(rider, new Location(0, 0), new Location(1, 1)));
    }

    @Test
    @DisplayName("Race: many riders, one driver — exactly one gets the ride")
    void concurrentRequestsOneWinner() throws InterruptedException {
        service.registerDriver(availableDriver("d1", 0, 0));

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger matched = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            Rider r = new Rider("r" + i, "rider" + i);
            pool.submit(() -> {
                try {
                    startGate.await();
                    service.requestTrip(r, new Location(0, 0), new Location(1, 1));
                    matched.incrementAndGet();
                } catch (IllegalStateException e) {
                    rejected.incrementAndGet(); // no driver left
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

        assertEquals(1, matched.get(), "only one rider can claim the single driver");
        assertEquals(threads - 1, rejected.get());
    }
}
