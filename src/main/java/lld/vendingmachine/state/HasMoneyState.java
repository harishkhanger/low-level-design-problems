package lld.vendingmachine.state;

import lld.vendingmachine.VendingMachine;
import lld.vendingmachine.model.Coin;
import lld.vendingmachine.model.Slot;

public class HasMoneyState implements VendingMachineState {
    private final VendingMachine vendingMachine;

    public HasMoneyState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertCoin(Coin coin) {
        vendingMachine.addCoin(coin);
    }

    @Override
    public void selectProduct(String code) {
        Slot slot = vendingMachine.getSlot(code);
        if (slot == null)throw new IllegalStateException("Invalid slot");
        if (!slot.isAvailable()) throw new IllegalStateException("Sold out: " + code);
        else vendingMachine.setSelectedCode(code);
        vendingMachine.setState(vendingMachine.getProductSelectedState());
    }

    @Override
    public void dispense() {
        throw new IllegalStateException("Select a product first");
    }

    @Override
    public void cancel() {
        vendingMachine.refund();
        vendingMachine.setState(vendingMachine.getIdleState());
    }
}
