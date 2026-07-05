package lld.fooddelivery.observer;

import lld.fooddelivery.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomerNotifier implements OrderObserver {
    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void onStatusChanged(Order order) {
        String message = "Hi " + order.getCustomer().getName()
            + ", your order " + order.getId() + " is now " + order.getStatus();
        sentMessages.add(message);
        System.out.println(message);
    }

    public List<String> getSentMessages() {
        return List.copyOf(sentMessages);
    }
}
