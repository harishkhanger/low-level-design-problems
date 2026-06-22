package lld.bookmyshow;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SeatLockManager {
    private final Clock clock;
    private final Duration lockTimeOut;
    private final Map<String, SeatLock> locks = new HashMap<>();
    private final Set<String> booked = new HashSet<>();

    public SeatLockManager(Clock clock, Duration lockTimeOut) {
        this.clock = clock;
        this.lockTimeOut = lockTimeOut;
    }

    public synchronized boolean lockSeats(List<Seat> seats, Show show, User user) {
        Instant now = clock.instant();
        for (Seat seat : seats) {
            String key = key(show, seat);
            if (booked.contains(key)) return false;
            SeatLock lock = locks.get(key);
            if (lock != null && lock.expiresAt().isAfter(now)
                    && !lock.userId.equals(user.id())) {
                return false;
            }
        }
        for (Seat seat : seats) {
            locks.put(key(show, seat), new SeatLock(user.id(), now.plus(lockTimeOut)));
        }
        return true;
    }

    public synchronized void releaseLocks(Show show, List<Seat> seats, User user) {
        for (Seat seat : seats) {
            String key = key(show, seat);
            SeatLock lock = locks.get(key);
            if (lock != null && lock.userId().equals(user.id())) {
                locks.remove(key);
            }
        }
    }

    public synchronized boolean confirmBookings(Show show, List<Seat> seats, User user) {
        Instant now = clock.instant();
        for (Seat seat : seats) {
            String key = key(show, seat);
            SeatLock lock = locks.get(key);
            boolean validHold = lock!=null
                    && lock.userId().equals(user.id())
                    && lock.expiresAt().isAfter(now);
            if (!validHold) {
                return false;
            }
        }

        for (Seat seat: seats){
            String key = key(show, seat);
            booked.add(key);
            locks.remove(key);
        }
        return true;
    }

    private String key(Show show, Seat seat) {
        return show.id() + "#" + seat.id();
    }

    private record SeatLock(String userId, Instant expiresAt) {
    }
}
