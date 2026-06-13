package lld.vendingmachine;

import lld.vendingmachine.model.Coin;
import lld.vendingmachine.model.Product;
import lld.vendingmachine.model.Slot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VendingMachineTest {

    private VendingMachine machine;
    private Slot cokeSlot;
    private Slot waterSlot;

    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
        cokeSlot = new Slot("A1", new Product("Coke", 1), 10, 2);
        waterSlot = new Slot("B1", new Product("Water", 2), 5, 1);
        machine.addSlot(cokeSlot);
        machine.addSlot(waterSlot);
    }

    @Test
    @DisplayName("A fresh machine starts in the idle state")
    void startsIdle() {
        assertSame(machine.getIdleState(), machine.getCurrentState());
    }

    @Test
    @DisplayName("Happy path: insert exact money, select, dispense — item out, no change, back to idle")
    void happyPath() {
        machine.insertCoin(Coin.TEN);
        assertSame(machine.getHasMoneyState(), machine.getCurrentState());

        machine.selectProduct("A1");
        assertSame(machine.getProductSelectedState(), machine.getCurrentState());

        machine.dispense();
        assertSame(machine.getIdleState(), machine.getCurrentState()); // reset
        assertEquals("Coke", machine.getLastDispensed().name());
        assertEquals(0, machine.getLastChange());
        assertEquals(1, cokeSlot.getQuantity()); // stock dropped 2 -> 1
        assertEquals(10, machine.getTotalSales());
    }

    @Test
    @DisplayName("Overpaying returns the correct change")
    void returnsChange() {
        machine.insertCoin(Coin.TEN);
        machine.insertCoin(Coin.TWO);
        machine.selectProduct("A1");
        machine.dispense();

        assertEquals(2, machine.getLastChange());
    }

    @Test
    @DisplayName("Total sales accumulate across purchases")
    void totalSalesAccumulate() {
        machine.insertCoin(Coin.TEN);
        machine.selectProduct("A1");
        machine.dispense();

        machine.insertCoin(Coin.FIVE);
        machine.selectProduct("B1");
        machine.dispense();

        assertEquals(15, machine.getTotalSales());
    }

    @Test
    @DisplayName("Selecting a product before inserting money is rejected")
    void selectBeforeMoneyThrows() {
        assertThrows(IllegalStateException.class, () -> machine.selectProduct("A1"));
    }

    @Test
    @DisplayName("Dispensing before selecting a product is rejected")
    void dispenseBeforeSelectThrows() {
        machine.insertCoin(Coin.TEN);
        assertThrows(IllegalStateException.class, () -> machine.dispense());
    }

    @Test
    @DisplayName("Dispensing without enough money is rejected")
    void insufficientFundsThrows() {
        machine.insertCoin(Coin.FIVE);
        machine.selectProduct("A1");
        assertThrows(IllegalStateException.class, () -> machine.dispense());
    }

    @Test
    @DisplayName("Selecting an unknown slot is rejected")
    void invalidSlotThrows() {
        machine.insertCoin(Coin.TEN);
        assertThrows(IllegalStateException.class, () -> machine.selectProduct("Z9"));
    }

    @Test
    @DisplayName("Selecting a sold-out slot is rejected")
    void soldOutThrows() {
        machine.insertCoin(Coin.FIVE);
        machine.selectProduct("B1");
        machine.dispense();
        assertEquals(0, waterSlot.getQuantity());

        machine.insertCoin(Coin.FIVE);
        assertThrows(IllegalStateException.class, () -> machine.selectProduct("B1"));
    }

    @Test
    @DisplayName("Cancelling refunds the balance and returns to idle")
    void cancelRefunds() {
        machine.insertCoin(Coin.TEN);
        machine.insertCoin(Coin.TWO);
        assertEquals(12, machine.getBalance());

        machine.cancel();
        assertEquals(0, machine.getBalance());
        assertSame(machine.getIdleState(), machine.getCurrentState());
    }

    @Test
    @DisplayName("A selected product can be changed before dispensing")
    void canChangeSelection() {
        machine.insertCoin(Coin.TEN);
        machine.selectProduct("A1");
        machine.selectProduct("B1");
        machine.dispense();

        assertEquals("Water", machine.getLastDispensed().name());
        assertEquals(5, machine.getLastChange());
    }
}
