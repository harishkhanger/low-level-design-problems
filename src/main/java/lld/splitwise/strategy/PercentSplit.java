package lld.splitwise.strategy;

import lld.splitwise.model.Split;
import lld.splitwise.model.User;

import java.util.ArrayList;
import java.util.List;

public class PercentSplit implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(List<User> users, List<Double> values, double totalAmounts) {
        List<Split> splitList = new ArrayList<>();
        double totalSum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            double current = (totalAmounts * values.get(i) / 100);
            totalSum += current;
            splitList.add(new Split(users.get(i), current));
        }
        if (Math.abs(totalAmounts - totalSum) > 0.01) throw new IllegalArgumentException("Invalid percentage");
        return splitList;
    }
}
