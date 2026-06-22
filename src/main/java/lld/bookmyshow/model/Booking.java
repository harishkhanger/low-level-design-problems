package lld.bookmyshow.model;

import java.util.List;

public class Booking {
    private final String id;
    private final Show show;
    private final List<Seat> seats;
    private final User user;
    private BookingStatus status;

    public Booking(String id, Show show, List<Seat> seats, User user, BookingStatus status) {
        this.id = id;
        this.show = show;
        this.seats = List.copyOf(seats);
        this.user = user;
        this.status = status;
    }

    public String getId() { return id; }
    public Show getShow() { return show; }
    public List<Seat> getSeats() { return seats; }
    public User getUser() { return user; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
