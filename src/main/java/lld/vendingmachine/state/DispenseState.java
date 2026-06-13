package lld.vendingmachine.state;

import lld.vendingmachine.VendingMachine;
import lld.vendingmachine.model.Coin;

public class DispenseState implements VendingMachineState {

    public DispenseState(VendingMachine vendingMachine) {
    }

    @Override
    public void insertCoin(Coin coin) {
        throw getIllegalStateException();
    }

    @Override
    public void selectProduct(String code) {
        throw getIllegalStateException();
    }

    @Override
    public void dispense() {
        throw getIllegalStateException();
    }

    @Override
    public void cancel() {
        throw getIllegalStateException();
    }


    private static IllegalStateException getIllegalStateException() {
        return new IllegalStateException("Please wait, dispensing is in process.");
    }
}
