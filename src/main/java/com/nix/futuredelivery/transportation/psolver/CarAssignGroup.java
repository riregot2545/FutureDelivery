package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.transportation.psolver.model.AssignCar;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class using for more convenient car assigning
 */
public class CarAssignGroup {
    private final List<AssignCar> assignList;

    @Getter
    private double groupPriority;
    @Getter
    private final Capacity capacity;

    private Capacity nextGroupCapacity;

    /**
     * Setter for {@code nextGroupCapacity}. Also this field connected to group priority calculation.
     *
     * @param nextGroupCapacity capacity of next car group.
     */
    public void setNextGroupCapacity(Capacity nextGroupCapacity) {
        this.nextGroupCapacity = nextGroupCapacity;
        this.groupPriority = nextGroupCapacity.getMaxVolume().getVolumeWeight() / capacity.getMaxVolume().getVolumeWeight();
    }

    /**
     * Constructs car group from list of cars that have same capacity.
     * @param carList cars with same capacity.
     */
    public CarAssignGroup(List<Car> carList) {
        this.assignList = carList.stream().map(AssignCar::new).collect(Collectors.toList());
        this.capacity = assignList.get(0).getCar().getCapacity();
    }

    /**
     * Getter for next available and most free car in group.
     * @return next car from group.
     */
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

    /**
     * Decrements car assign. May throw {@code IllegalArgumentException} if car is not presented in group.
     *
     * @param carToResetAssign car to decrement assign.
     */
    public void decrementCarAssign(Car carToResetAssign) {
        AssignCar resetCar = assignList.stream()
                .filter((car) -> car.getCar().equals(carToResetAssign))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Car is not contained in the assign list"));
        resetCar.decrimentAssign();
    }

    /**
     * Gets this group load based on group priority and capacity, count of assigns and next group capacity.
     * @return double representation of loading.
     */
    public double getGroupLoad() {
        int assignSum = assignList.stream().mapToInt(AssignCar::getAssignedCount).sum();
        return groupPriority * capacity.getMaxVolume().getVolumeWeight() +
                assignSum * nextGroupCapacity.getMaxVolume().getVolumeWeight();
    }
}
