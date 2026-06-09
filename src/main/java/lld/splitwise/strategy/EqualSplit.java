package lld.splitwise.strategy;

import lld.splitwise.model.Split;
import lld.splitwise.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class EqualSplit implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(List<User> users, List<Double> values, double totalAmounts) {
        double split = totalAmounts / users.size();
        return users.stream().map(a -> new Split(a, split)).collect(Collectors.toList());
    }
}
