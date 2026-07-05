package lld.fooddelivery.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final Customer customer;
    private final Restaurant restaurant;
    private final Map<MenuItem, Integer> items = new LinkedHashMap<>();

    public Cart(Customer customer, Restaurant restaurant) {
        this.customer = customer;
        this.restaurant = restaurant;
    }

    public void addItem(MenuItem item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (restaurant.findItem(item.getId()).isEmpty()) {
            throw new IllegalArgumentException("item does not belong to this restaurant");
        }
        if (!item.isAvailable()) {
            throw new IllegalStateException("item is not available: " + item.getName());
        }
        items.merge(item, quantity, Integer::sum);
    }

    public void removeItem(MenuItem item) {
        items.remove(item);
    }

    public Map<MenuItem, Integer> getItems() {
        return Map.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotal() {
        return items.entrySet().stream()
            .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
            .sum();
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
}
