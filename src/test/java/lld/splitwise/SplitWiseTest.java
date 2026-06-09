package lld.splitwise;

import lld.splitwise.model.User;
import lld.splitwise.service.SplitWise;
import lld.splitwise.strategy.EqualSplit;
import lld.splitwise.strategy.ExactSplit;
import lld.splitwise.strategy.PercentSplit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitWiseTest {

    private SplitWise splitwise;
    private User alice;
    private User bob;
    private User charlie;

    @BeforeEach
    void setUp() {
        splitwise = new SplitWise();
        alice = new User("u1", "Alice");
        bob = new User("u2", "Bob");
        charlie = new User("u3", "Charlie");
    }

    @Test
    @DisplayName("Equal split: Alice pays 900, Bob and Charlie each owe her 300")
    void equalSplit() {
        splitwise.addExpense(alice, 900, new EqualSplit(),
                List.of(alice, bob, charlie), List.of(), "Dinner");

        assertEquals(300.0, splitwise.getBalance(bob, alice), 0.001);
        assertEquals(300.0, splitwise.getBalance(charlie, alice), 0.001);
        assertEquals(-300.0, splitwise.getBalance(alice, bob), 0.001);
        assertEquals(0.0, splitwise.getBalance(bob, charlie), 0.001);
    }

    @Test
    @DisplayName("Exact split: each participant owes their specified amount")
    void exactSplit() {
        splitwise.addExpense(alice, 400, new ExactSplit(),
                List.of(alice, bob, charlie), List.of(50.0, 150.0, 200.0), "Groceries");

        assertEquals(150.0, splitwise.getBalance(bob, alice), 0.001);
        assertEquals(200.0, splitwise.getBalance(charlie, alice), 0.001);
    }

    @Test
    @DisplayName("Percent split: percentages convert to amounts")
    void percentSplit() {
        splitwise.addExpense(alice, 1000, new PercentSplit(),
                List.of(alice, bob, charlie), List.of(40.0, 30.0, 30.0), "Trip");

        assertEquals(300.0, splitwise.getBalance(bob, alice), 0.001);
        assertEquals(300.0, splitwise.getBalance(charlie, alice), 0.001);
    }

    @Test
    @DisplayName("Settling a debt reduces the amount owed")
    void settleDebt() {
        splitwise.addExpense(alice, 200, new EqualSplit(),
                List.of(alice, bob), List.of(), "Cab");
        assertEquals(100.0, splitwise.getBalance(bob, alice), 0.001);

        splitwise.settleDebt(bob, alice, 60.0);
        assertEquals(40.0, splitwise.getBalance(bob, alice), 0.001);
    }

    @Test
    @DisplayName("Balances accumulate across expenses and can net to zero")
    void balancesNetAcrossExpenses() {
        splitwise.addExpense(alice, 100, new EqualSplit(), List.of(alice, bob), List.of(), "Lunch");
        splitwise.addExpense(bob, 100, new EqualSplit(), List.of(alice, bob), List.of(), "Coffee");

        assertEquals(0.0, splitwise.getBalance(bob, alice), 0.001);
        assertEquals(0.0, splitwise.getBalance(alice, bob), 0.001);
    }

    @Test
    @DisplayName("Each added expense is recorded in the history")
    void expensesAreRecorded() {
        splitwise.addExpense(alice, 100, new EqualSplit(), List.of(alice, bob), List.of(), "Lunch");
        splitwise.addExpense(bob, 200, new EqualSplit(), List.of(alice, bob), List.of(), "Dinner");

        assertEquals(2, splitwise.getExpenseList().size());
        assertEquals("Lunch", splitwise.getExpenseList().get(0).getDescription());
    }

    @Test
    @DisplayName("Exact splits that don't sum to the total are rejected")
    void exactSplitValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                splitwise.addExpense(alice, 200, new ExactSplit(),
                        List.of(alice, bob), List.of(50.0, 100.0), "Bad"));
    }

    @Test
    @DisplayName("Percentages that don't sum to 100 are rejected")
    void percentSplitValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                splitwise.addExpense(alice, 100, new PercentSplit(),
                        List.of(alice, bob), List.of(40.0, 40.0), "Bad")); 
    }
}
