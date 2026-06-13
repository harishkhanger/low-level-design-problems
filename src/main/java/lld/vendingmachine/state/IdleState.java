package lld.vendingmachine.state;

import lld.vendingmachine.VendingMachine;
import lld.vendingmachine.model.Coin;

public class IdleState implements VendingMachineState {
    private final VendingMachine vendingMachine;

    public IdleState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertCoin(Coin coin) {
        vendingMachine.addCoin(coin);
        vendingMachine.setState(vendingMachine.getHasMoneyState());
    }

    @Override
    public void selectProduct(String code) {
        throw new IllegalStateException("Insert the money first");
    }

    @Override
    public void dispense() {
        throw new IllegalStateException("No product selected");
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("No money to refund");
    }
}
