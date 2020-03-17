package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that aggregate car groups for assigning and manage it for
 * most fairly loading
 */
public class CarAssigner {
    private final List<CarAssignGroup> carGroups;
    private CarAssignGroup currentGroup;
    private int currentGroupKey;

    /**
     * Constructs assigner from list of cars, that subdivided and grouped by it's
     * capacity. May throw {@code IllegalArgumentException} if car list is empty.
     *
     * @param carList not empty car list.
     */
    public CarAssigner(List<Car> carList) {
        if (carList.isEmpty())
            throw new IllegalArgumentException("Car list is empty");

        this.carGroups = new ArrayList<>();
        this.currentGroupKey = 0;

        Map<Capacity, List<Car>> mappedGroups = carList.stream()
                .collect(Collectors.groupingBy(Car::getCapacity));

        fillGroups(mappedGroups);
        this.currentGroup = carGroups.get(currentGroupKey);
    }

    /**
     * Fills group data and calculates group capacity dependencies. Last group takes approximate
     * next group capacity based of double last and subtraction previous group capacity.
     * @param mappedGroups groups grouped by capacity.
     */
    private void fillGroups(Map<Capacity, List<Car>> mappedGroups) {
        List<Capacity> sortedKeys = getSortedKeys(mappedGroups);

        for (int i = 0; i < sortedKeys.size(); i++) {
            CarAssignGroup carAssignGroup = new CarAssignGroup(mappedGroups.get(sortedKeys.get(i)));
            if (sortedKeys.size() == 1) {
                carAssignGroup.setNextGroupCapacity(sortedKeys.get(0));
            } else if (i < (sortedKeys.size() - 1)) {
                carAssignGroup.setNextGroupCapacity(mappedGroups.get(sortedKeys.get(i + 1)).get(0).getCapacity());
            } else {
                double previousCapacity = mappedGroups.get(sortedKeys.get(i - 1)).get(0).getCapacity().getMaxVolume().getVolumeWeight();
                double currentCapacity = carAssignGroup.getCapacity().getMaxVolume().getVolumeWeight();
                carAssignGroup.setNextGroupCapacity(new Capacity(new Volume(2 * currentCapacity - previousCapacity)));
            }
            carGroups.add(carAssignGroup);
        }
    }

    /**
     * Retrieves and sorts keys by capacity.
     * @param mappedGroups groups grouped by capacity.
     * @return ordered capacity list
     */
    private List<Capacity> getSortedKeys(Map<Capacity, List<Car>> mappedGroups) {
        return mappedGroups.keySet().stream()
                .sorted(Comparator.comparingDouble(c -> c.getMaxVolume().getVolumeWeight()))
                .collect(Collectors.toList());
    }

    /**
     * Resets current group level to first group.
     */
    public void resetGroupLevel() {
        currentGroupKey = 0;
        currentGroup = carGroups.get(currentGroupKey);
    }

    /**
     * Gets most free car with default parameter of refresh - true.
     * @return most free car.
     */
    public Car getNextMostFreeCar() {
        return getNextMostFreeCar(true);
    }

    /**
     * Gets most free car with changeable parameter of refresh.
     * @return most free car.
     */
    public Car getNextMostFreeCar(boolean refreshWights) {
        if (refreshWights)
            refreshGroupWeight();
        return currentGroup.getNextCar();
    }

    /**
     * Gets group of car argument, decrement it's assign and reset car fullness. May throw
     * {@code IllegalArgumentException} if car is not presented in any group.
     *
     * @param car
     */
    public void decrementCarAssign(Car car) {
        CarAssignGroup carGroup = carGroups.stream()
                .filter(c -> c.getCapacity().equals(car.getCapacity()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Car is not contained in the assign list"));
        carGroup.decrementCarAssign(car);
        car.resetFullness();
    }

    /**
     * Changes current group to group of next level. Not advisable to use without group weight
     * recalculation.
     * @return return optional of next group capacity, or empty optional if
     * current group is last group for assign.
     */
    public Optional<Capacity> incrementGroupLevel() {
        if (currentGroupKey == carGroups.size() - 1)
            return Optional.empty();
        else {
            currentGroupKey++;
            currentGroup = carGroups.get(currentGroupKey);
            return Optional.of(currentGroup.getCapacity());
        }
    }

    /**
     * Refreshes all group weights and change current group if exists weight less than current.
     */
    private void refreshGroupWeight() {
        Optional<CarAssignGroup> minLoadedGroup = carGroups.stream().min(Comparator.comparingDouble(CarAssignGroup::getGroupLoad));
        if (minLoadedGroup.isPresent()) {
            CarAssignGroup group = minLoadedGroup.get();
            currentGroupKey = carGroups.indexOf(group);
            currentGroup = group;
        }
    }
}
