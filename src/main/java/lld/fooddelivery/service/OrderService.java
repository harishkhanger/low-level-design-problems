package lld.fooddelivery.service;

import lld.fooddelivery.model.Cart;
import lld.fooddelivery.model.DeliveryPartner;
import lld.fooddelivery.model.Order;
import lld.fooddelivery.model.OrderStatus;
import lld.fooddelivery.model.Restaurant;
import lld.fooddelivery.observer.OrderObserver;
import lld.fooddelivery.strategy.AssignmentStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final List<DeliveryPartner> partners = new ArrayList<>();
    private final AssignmentStrategy assignmentStrategy;
    private final AtomicLong orderCounter = new AtomicLong(1);

    public OrderService(AssignmentStrategy assignmentStrategy) {
        this.assignmentStrategy = assignmentStrategy;
    }

    public void registerRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public void registerPartner(DeliveryPartner partner) {
        partners.add(partner);
    }

    public List<Restaurant> getRestaurants() {
        return List.copyOf(restaurants);
    }

    public Order placeOrder(Cart cart, OrderObserver... observers) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("cannot place an order with an empty cart");
        }
        Order order = new Order("ORD-" + orderCounter.getAndIncrement(), cart);
        for (OrderObserver observer : observers) {
            order.addObserver(observer);
        }
        return order;
    }

    public void confirm(Order order) {
        order.advanceTo(OrderStatus.CONFIRMED);
    }

    public void startPreparing(Order order) {
        order.advanceTo(OrderStatus.PREPARING);
    }

    public void dispatch(Order order) {
        Optional<DeliveryPartner> assigned = assignmentStrategy.assign(partners, order);
        DeliveryPartner partner = assigned.orElseThrow(
            () -> new IllegalStateException("no delivery partner available"));
        partner.setAvailable(false);
        order.assignPartner(partner);
        order.advanceTo(OrderStatus.OUT_FOR_DELIVERY);
    }

    public void markDelivered(Order order) {
        order.advanceTo(OrderStatus.DELIVERED);
        freePartner(order);
    }

    public void cancel(Order order) {
        order.advanceTo(OrderStatus.CANCELLED);
        freePartner(order);
    }

    private void freePartner(Order order) {
        DeliveryPartner partner = order.getDeliveryPartner();
        if (partner != null) {
            partner.setAvailable(true);
        }
    }
}
