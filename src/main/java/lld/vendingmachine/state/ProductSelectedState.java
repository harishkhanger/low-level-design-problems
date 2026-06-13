package lld.vendingmachine.state;

import lld.vendingmachine.VendingMachine;
import lld.vendingmachine.model.Coin;
import lld.vendingmachine.model.Slot;

public class ProductSelectedState implements VendingMachineState {

    private final VendingMachine vendingMachine;

    public ProductSelectedState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertCoin(Coin coin) {
        vendingMachine.addCoin(coin);
    }

    @Override
    public void selectProduct(String code) {
        Slot slot = vendingMachine.getSlot(code);
        if (slot == null) throw new IllegalStateException("Invalid slot: " + code);
        if (!slot.isAvailable()) throw new IllegalStateException("Sold out: " + code);
        vendingMachine.setSelectedCode(code);
    }

    @Override
    public void dispense() {
        Slot slot = vendingMachine.getSlot(vendingMachine.getSelectedCode());
        if (vendingMachine.getBalance()<slot.getPrice())throw new IllegalStateException("Insufficient fund");
        else vendingMachine.setState(vendingMachine.getDispenseState());
        vendingMachine.dispenseSelected();
        vendingMachine.setState(vendingMachine.getIdleState());
    }

    @Override
    public void cancel() {
        vendingMachine.refund();
        vendingMachine.setState(vendingMachine.getIdleState());
    }
}
