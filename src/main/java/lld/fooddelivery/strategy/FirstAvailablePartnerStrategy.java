package lld.fooddelivery.strategy;

import lld.fooddelivery.model.DeliveryPartner;
import lld.fooddelivery.model.Order;

import java.util.List;
import java.util.Optional;

public class FirstAvailablePartnerStrategy implements AssignmentStrategy {

    @Override
    public Optional<DeliveryPartner> assign(List<DeliveryPartner> partners, Order order) {
        return partners.stream()
            .filter(DeliveryPartner::isAvailable)
            .findFirst();
    }
}
