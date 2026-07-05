package lld.fooddelivery.strategy;

import lld.fooddelivery.model.DeliveryPartner;
import lld.fooddelivery.model.Location;
import lld.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestAvailablePartnerStrategy implements AssignmentStrategy {

    @Override
    public Optional<DeliveryPartner> assign(List<DeliveryPartner> partners, Order order) {
        Location pickup = order.getRestaurant().getLocation();
        return partners.stream()
            .filter(DeliveryPartner::isAvailable)
            .min(Comparator.comparingDouble(p -> p.getLocation().distanceTo(pickup)));
    }
}
