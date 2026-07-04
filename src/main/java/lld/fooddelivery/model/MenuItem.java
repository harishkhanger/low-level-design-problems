package lld.fooddelivery.model;

public class MenuItem {
    private final String id;
    private final String name;
    private final double price;
    private boolean available;

    public MenuItem(String id, String name, double price) {
        if (price < 0) {
            throw new IllegalArgumentException("price must not be negative");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
