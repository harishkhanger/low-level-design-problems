package lld.parkinglot.model;

public enum SpotSize {
    SMALL(1), MEDIUM(2), LARGE(3);

    private final int value;

    SpotSize(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
