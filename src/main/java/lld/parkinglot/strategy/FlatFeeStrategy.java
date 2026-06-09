package lld.parkinglot.strategy;

import lld.parkinglot.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FlatFeeStrategy implements FeeStrategy{

    private final int ratePerHour;

    public FlatFeeStrategy(int ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double calculateFee(Ticket ticket, LocalDateTime exitTime) {
        Duration duration = Duration.between(ticket.entryTime(), exitTime);
        return Math.ceil(duration.toMinutes()/60.0) * ratePerHour;
    }
}
