package com.nix.futuredelivery.distribution.psolver;

import com.nix.futuredelivery.distribution.psolver.model.AssignCar;
import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class AssignCarGroup {
    private final List<AssignCar> assignList;

    @Getter
    private double groupPriority;
    @Getter
    private final Capacity capacity;

    private Capacity nextGroupCapacity;

    public void setNextGroupCapacity(Capacity nextGroupCapacity) {
        this.nextGroupCapacity = nextGroupCapacity;
        this.groupPriority = nextGroupCapacity.getMaxVolume().getVolume() / capacity.getMaxVolume().getVolume();
    }

    public AssignCarGroup(List<Car> carList) {
        this.assignList = carList.stream().map(AssignCar::new).collect(Collectors.toList());
        this.capacity = assignList.get(0).getCar().getCapacity();
    }

    public Car getNextCar() {
        Optional<AssignCar> minOfAssigned = assignList.stream()
                .min(Comparator.comparingDouble(AssignCar::getAssignedCount));
        if (minOfAssigned.isPresent()) {
            AssignCar assignCar = minOfAssigned.get();
            assignCar.incrementAssign();
            return assignCar.getCar();
        }
        throw new IllegalStateException("Assign stream is empty");
    }

    public void resetAssignCar(Car carToResetAssign) {
        AssignCar resetCar = assignList.stream()
                .filter((car) -> car.getCar().equals(carToResetAssign))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Car is not contained in the assign list"));
        resetCar.decrimentAssign();
    }

    public double getGroupLoad() {
        int assignSum = assignList.stream().mapToInt(AssignCar::getAssignedCount).sum();
        return groupPriority * capacity.getMaxVolume().getVolume() + assignSum * nextGroupCapacity.getMaxVolume().getVolume();
    }
}
