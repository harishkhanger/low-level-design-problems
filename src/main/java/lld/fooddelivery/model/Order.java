package lld.fooddelivery.model;

import lld.fooddelivery.observer.OrderObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {
    private final String id;
    private final Customer customer;
    private final Restaurant restaurant;
    private final Map<MenuItem, Integer> items;
    private final double total;
    private final List<OrderObserver> observers = new ArrayList<>();
    private OrderStatus status;
    private DeliveryPartner deliveryPartner;

    public Order(String id, Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("cannot create an order from an empty cart");
        }
        this.id = id;
        this.customer = cart.getCustomer();
        this.restaurant = cart.getRestaurant();
        this.items = cart.getItems();
        this.total = cart.getTotal();
        this.status = OrderStatus.PLACED;
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void advanceTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException("illegal transition: " + status + " -> " + next);
        }
        status = next;
        for (OrderObserver observer : observers) {
            observer.onStatusChanged(this);
        }
    }

    public void assignPartner(DeliveryPartner partner) {
        this.deliveryPartner = partner;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Map<MenuItem, Integer> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }
}
