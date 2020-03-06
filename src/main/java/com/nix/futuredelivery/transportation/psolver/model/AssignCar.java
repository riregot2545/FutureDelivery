package com.nix.futuredelivery.transportation.psolver.model;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import lombok.Getter;


public class AssignCar {
    @Getter
    private Car car;
    @Getter
    private int assignedCount;

    public AssignCar(Long id, String model, Capacity capacity) {
        this.car = new Car(id, model, capacity);
        this.assignedCount = 0;
    }

    public AssignCar(Car car) {
        this.car = new Car(car.getId(), car.getModel(), car.getCapacity());
        this.assignedCount = 0;
    }

    public int incrementAssign() {
        return ++assignedCount;
    }

    public int decrimentAssign() {
        return --assignedCount;
    }
}
