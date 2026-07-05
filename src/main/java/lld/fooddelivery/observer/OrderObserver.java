package lld.fooddelivery.observer;

import lld.fooddelivery.model.Order;

public interface OrderObserver {
    void onStatusChanged(Order order);
}
