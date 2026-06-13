package lld.vendingmachine.model;

public class Slot {
    private final String code;
    private final Product product;
    private final int price;
    private int quantity;

    public Slot(String code, Product product, int price, int quantity) {
        this.code = code;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public void dispense(){
        if (quantity==0)throw new IllegalStateException("No item exist on the slot: " + code);
        quantity--;
    }

    public void restock(int count){
        quantity+=count;
    }

    public String getCode() {
        return code;
    }

    public Product getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

