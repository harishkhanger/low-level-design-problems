package lld.vendingmachine.model;

public enum Coin {
    ONE(1), TWO(2), FIVE(5), TEN(10);

    private final int coinValue;
    Coin(int price){
        this.coinValue = price;
    }

    public int getCoinValue() {
        return coinValue;
    }
}
