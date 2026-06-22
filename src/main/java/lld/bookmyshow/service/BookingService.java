package lld.bookmyshow.service;

import lld.bookmyshow.model.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {
    private final SeatLockManager seatLockManager;
    private final PaymentGateway paymentGateway;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    public BookingService(SeatLockManager seatLockManager, PaymentGateway paymentGateway) {
        this.seatLockManager = seatLockManager;
        this.paymentGateway = paymentGateway;
    }

    public Booking book (Show show, List<Seat> seats, User user) {
        if (!seatLockManager.lockSeats(seats, show, user)) throw new IllegalStateException("Seats are locked already");
        String id = "BKG-" + atomicInteger.incrementAndGet();
        boolean paid = paymentGateway.pay(user);
        if (paid){
            seatLockManager.confirmBookings(show, seats, user);
            return new Booking(id, show, seats, user, BookingStatus.CONFIRMED);
        }else {
            seatLockManager.releaseLocks(show, seats, user);
            return new Booking(id, show, seats, user, BookingStatus.EXPIRED);
        }
    }
}
