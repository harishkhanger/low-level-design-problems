package lld.splitwise.strategy;

import lld.splitwise.model.Split;
import lld.splitwise.model.User;

import java.util.ArrayList;
import java.util.List;

public class ExactSplit implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(List<User> users, List<Double> values, double totalAmounts) {
        List<Split> splitList = new ArrayList<>();
        double totalSum = 0.0;
        for (int i = 0; i < users.size(); i++) {
            double currentValue = values.get(i);
            totalSum += currentValue;
            splitList.add(new Split(users.get(i), currentValue));
        }
        if (Math.abs(totalAmounts - totalSum) > 0.001) throw new IllegalArgumentException("Invalid split values");
        return splitList;
    }
}
