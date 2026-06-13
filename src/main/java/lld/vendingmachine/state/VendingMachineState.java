package lld.vendingmachine.state;

import lld.vendingmachine.model.Coin;

public interface VendingMachineState {
    void insertCoin(Coin coin);
    void selectProduct(String code);
    void dispense();
    void cancel();
}
