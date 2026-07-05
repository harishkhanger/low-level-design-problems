package lld.fooddelivery;

import lld.fooddelivery.model.Cart;
import lld.fooddelivery.model.Customer;
import lld.fooddelivery.model.DeliveryPartner;
import lld.fooddelivery.model.Location;
import lld.fooddelivery.model.MenuItem;
import lld.fooddelivery.model.Order;
import lld.fooddelivery.model.OrderStatus;
import lld.fooddelivery.model.Restaurant;
import lld.fooddelivery.observer.CustomerNotifier;
import lld.fooddelivery.service.OrderService;
import lld.fooddelivery.strategy.NearestAvailablePartnerStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodDeliveryTest {

    private Restaurant restaurantWithMenu() {
        Restaurant r = new Restaurant("R1", "Tasty Bites", new Location(0, 0));
        r.addMenuItem(new MenuItem("M1", "Burger", 8.0));
        r.addMenuItem(new MenuItem("M2", "Fries", 3.5));
        return r;
    }

    @Test
    void cartTotalsQuantitiesTimesPrice() {
        Restaurant r = restaurantWithMenu();
        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 2);
        cart.addItem(r.findItem("M2").orElseThrow(), 1);
        assertEquals(19.5, cart.getTotal(), 1e-9);
    }

    @Test
    void addingItemFromAnotherRestaurantRejected() {
        Restaurant r = restaurantWithMenu();
        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        MenuItem foreign = new MenuItem("X9", "Sushi", 12.0);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(foreign, 1));
    }

    @Test
    void happyPathMovesThroughEveryStatus() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        service.registerRestaurant(r);
        service.registerPartner(new DeliveryPartner("P1", "Sam", new Location(1, 1)));

        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        Order order = service.placeOrder(cart);

        assertEquals(OrderStatus.PLACED, order.getStatus());
        service.confirm(order);
        service.startPreparing(order);
        service.dispatch(order);
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
        service.markDelivered(order);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void illegalTransitionThrows() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        Order order = service.placeOrder(cart);

        assertThrows(IllegalStateException.class, () -> service.markDelivered(order));
    }

    @Test
    void nearestPartnerIsChosen() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        service.registerRestaurant(r);
        DeliveryPartner far = new DeliveryPartner("P_far", "Far", new Location(10, 10));
        DeliveryPartner near = new DeliveryPartner("P_near", "Near", new Location(1, 0));
        service.registerPartner(far);
        service.registerPartner(near);

        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        Order order = service.placeOrder(cart);
        service.confirm(order);
        service.startPreparing(order);
        service.dispatch(order);

        assertEquals("P_near", order.getDeliveryPartner().getId());
        assertFalse(near.isAvailable());
        assertTrue(far.isAvailable());
    }

    @Test
    void deliveredPartnerBecomesAvailableAgain() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        service.registerRestaurant(r);
        DeliveryPartner p = new DeliveryPartner("P1", "Sam", new Location(1, 1));
        service.registerPartner(p);

        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        Order order = service.placeOrder(cart);
        service.confirm(order);
        service.startPreparing(order);
        service.dispatch(order);
        assertFalse(p.isAvailable());

        service.markDelivered(order);
        assertTrue(p.isAvailable());
    }

    @Test
    void dispatchWithNoPartnerThrows() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        service.registerRestaurant(r);

        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        Order order = service.placeOrder(cart);
        service.confirm(order);
        service.startPreparing(order);

        assertThrows(IllegalStateException.class, () -> service.dispatch(order));
    }

    @Test
    void customerIsNotifiedOnEveryStatusChange() {
        Restaurant r = restaurantWithMenu();
        OrderService service = new OrderService(new NearestAvailablePartnerStrategy());
        service.registerRestaurant(r);
        service.registerPartner(new DeliveryPartner("P1", "Sam", new Location(1, 1)));

        Cart cart = new Cart(new Customer("C1", "Alice", "12 Main St"), r);
        cart.addItem(r.findItem("M1").orElseThrow(), 1);
        CustomerNotifier notifier = new CustomerNotifier();
        Order order = service.placeOrder(cart, notifier);

        service.confirm(order);
        service.startPreparing(order);
        service.dispatch(order);
        service.markDelivered(order);

        assertEquals(4, notifier.getSentMessages().size());
    }
}
