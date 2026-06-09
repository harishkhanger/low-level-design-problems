package lld.parkinglot.strategy;

import lld.parkinglot.model.Ticket;

import java.time.LocalDateTime;

public interface FeeStrategy {
    double calculateFee(Ticket ticket, LocalDateTime exitTime);
}
