package lld.parkinglot.model;

import static lld.parkinglot.model.SpotSize.*;

public enum VehicleType {
    MOTOR_CYCLE(SMALL),
    CAR(MEDIUM),
    BUS(LARGE);

    private final SpotSize requiredSize;

    VehicleType(SpotSize spotSize){
        this.requiredSize = spotSize;
    }

    public SpotSize getRequiredSize() {
        return requiredSize;
    }
}
