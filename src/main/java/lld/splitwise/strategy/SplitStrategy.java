package lld.splitwise.strategy;

import lld.splitwise.model.Split;
import lld.splitwise.model.User;

import java.util.List;

public interface SplitStrategy {
    List<Split> calculateSplits(List<User> users, List<Double> values, double totalAmounts);
}
