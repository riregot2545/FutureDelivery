package com.nix.futuredelivery.transportation.psolver.model;

import com.nix.futuredelivery.entity.Car;
import lombok.Getter;

/**
 * Polar solver model class used to compose car and it's load in group.
 */
public class AssignCar {
    @Getter
    private Car car;
    @Getter
    private int assignedCount;

    public AssignCar(Car car) {
        this.car = new Car(car.getId(), car.getModel(), car.getCapacity(), car.getConsumption());
        this.assignedCount = 0;
    }

    public int incrementAssign() {
        return ++assignedCount;
    }

    public int decrimentAssign() {
        return --assignedCount;
    }
}
