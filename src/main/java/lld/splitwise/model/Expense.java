package lld.splitwise.model;

import java.util.List;

public class Expense {
    private final User userPaidBy;
    private final double amount;
    private final List<Split> splitList;
    private final String description;

    public Expense(User userPaidBy, double amount, List<Split> splitList, String description) {
        this.userPaidBy = userPaidBy;
        this.amount = amount;
        this.splitList = splitList;
        this.description = description;
    }

    public User getUserPaidBy() {
        return userPaidBy;
    }

    public Double getAmount() {
        return amount;
    }

    public List<Split> getSplitList() {
        return splitList;
    }

    public String getDescription() {
        return description;
    }
}
