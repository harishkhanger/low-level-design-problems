package lld.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Restaurant {
    private final String id;
    private final String name;
    private final List<MenuItem> menu = new ArrayList<>();

    public Restaurant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addMenuItem(MenuItem item) {
        menu.add(item);
    }

    public List<MenuItem> getMenu() {
        return List.copyOf(menu);
    }

    public Optional<MenuItem> findItem(String itemId) {
        return menu.stream().filter(i -> i.getId().equals(itemId)).findFirst();
    }
}
