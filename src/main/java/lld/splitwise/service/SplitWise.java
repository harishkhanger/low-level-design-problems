package lld.splitwise.service;

import lld.splitwise.model.Expense;
import lld.splitwise.model.Split;
import lld.splitwise.model.User;
import lld.splitwise.strategy.SplitStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
TODO: Need to add group support as well
 */
public class SplitWise {
    private final Map<User, Map<User, Double>> balances;
    private final List<Expense> expenseList;

    public SplitWise() {
        this.balances = new HashMap<>();
        this.expenseList = new ArrayList<>();
    }

    public void addExpense(User paidBy, double amount, SplitStrategy splitStrategy,
                           List<User> participants, List<Double> values, String description){
        List<Split> splitList = splitStrategy.calculateSplits(participants, values, amount);
        for (Split split: splitList){
            if (split.getUser().equals(paidBy)){
                continue;
            }else {
                adjust(split.getUser(), paidBy, split.getAmount());
            }
        }
        expenseList.add(new Expense(paidBy, amount, splitList, description));
    }

    public void settleDebt(User from, User to, Double amount){
        adjust(from, to, -amount);
    }

    public Double getBalance(User of, User with){
        return balances.getOrDefault(of, Map.of()).getOrDefault(with, 0.0);
    }

    private void adjust(User from, User to, Double delta){
        balances.computeIfAbsent(from, y->new HashMap<>()).merge(to, delta, Double::sum);
        balances.computeIfAbsent(to, y->new HashMap<>()).merge(from, -delta, Double::sum);
    }

    public List<Expense> getExpenseList() {
        return List.copyOf(expenseList);
    }
}
