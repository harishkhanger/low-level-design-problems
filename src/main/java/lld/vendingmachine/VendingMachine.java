package lld.vendingmachine;

import lld.vendingmachine.model.Coin;
import lld.vendingmachine.model.Product;
import lld.vendingmachine.model.Slot;
import lld.vendingmachine.state.DispenseState;
import lld.vendingmachine.state.HasMoneyState;
import lld.vendingmachine.state.IdleState;
import lld.vendingmachine.state.ProductSelectedState;
import lld.vendingmachine.state.VendingMachineState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VendingMachine {

    private final VendingMachineState idleState;
    private final VendingMachineState hasMoneyState;
    private final VendingMachineState productSelectedState;
    private final VendingMachineState dispenseState;

    private VendingMachineState currentState;

    private final Map<String, Slot> inventory = new HashMap<>();
    private final List<Coin> insertedCoins = new ArrayList<>();
    private String selectedCode;
    private int totalSales;

    private Product lastDispensed;
    private int lastChange;

    public VendingMachine() {
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        productSelectedState = new ProductSelectedState(this);
        dispenseState = new DispenseState(this);
        currentState = idleState; // a fresh machine is idle
    }


    public void addSlot(Slot slot) {
        inventory.put(slot.getCode(), slot);
    }


    public void insertCoin(Coin coin) {
        currentState.insertCoin(coin);
    }

    public void selectProduct(String code) {
        currentState.selectProduct(code);
    }

    public void dispense() {
        currentState.dispense();
    }

    public void cancel() {
        currentState.cancel();
    }

    public void setState(VendingMachineState state) {
        this.currentState = state;
    }

    public VendingMachineState getCurrentState() {
        return currentState;
    }

    public VendingMachineState getIdleState() {
        return idleState;
    }

    public VendingMachineState getHasMoneyState() {
        return hasMoneyState;
    }

    public VendingMachineState getProductSelectedState() {
        return productSelectedState;
    }

    public VendingMachineState getDispenseState() {
        return dispenseState;
    }

    public void addCoin(Coin coin) {
        insertedCoins.add(coin);
    }

    public int getBalance() {
        return insertedCoins.stream().mapToInt(Coin::getCoinValue).sum();
    }

    public Slot getSlot(String code) {
        return inventory.get(code);
    }

    public void setSelectedCode(String code) {
        this.selectedCode = code;
    }

    public String getSelectedCode() {
        return selectedCode;
    }

    public List<Coin> refund() {
        List<Coin> refunded = new ArrayList<>(insertedCoins);
        resetTransaction();
        return refunded;
    }

    public int dispenseSelected() {
        Slot slot = inventory.get(selectedCode);
        slot.dispense(); // decrement stock (throws if empty)
        int change = getBalance() - slot.getPrice();
        lastDispensed = slot.getProduct();
        lastChange = change;
        totalSales += slot.getPrice();
        resetTransaction();
        return change;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public Product getLastDispensed() {
        return lastDispensed;
    }

    public int getLastChange() {
        return lastChange;
    }

    private void resetTransaction() {
        insertedCoins.clear();
        selectedCode = null;
    }
}
