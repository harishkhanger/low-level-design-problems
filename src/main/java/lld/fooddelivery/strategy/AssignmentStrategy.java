package lld.fooddelivery.strategy;

import lld.fooddelivery.model.DeliveryPartner;
import lld.fooddelivery.model.Order;

import java.util.List;
import java.util.Optional;

public interface AssignmentStrategy {
    Optional<DeliveryPartner> assign(List<DeliveryPartner> partners, Order order);
}
