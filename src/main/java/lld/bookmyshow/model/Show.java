package lld.bookmyshow.model;

import java.time.Instant;
import java.util.List;


public record Show(String id, Movie movie, String city, String theatre,
                   Instant startTime, List<Seat> seats) {
    public Show {
        seats = List.copyOf(seats);
    }
}
