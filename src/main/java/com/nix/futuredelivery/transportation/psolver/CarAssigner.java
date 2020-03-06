package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;

import java.util.*;
import java.util.stream.Collectors;

public class CarAssigner {
    private final List<CarAssignGroup> carGroups;
    private CarAssignGroup currentGroup;
    private int currentGroupKey;

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

    private void fillGroups(Map<Capacity, List<Car>> mappedGroups) {
        List<Capacity> sortedKeys = getSortedKeys(mappedGroups);

        for (int i = 0; i < sortedKeys.size(); i++) {
            CarAssignGroup carAssignGroup = new CarAssignGroup(mappedGroups.get(sortedKeys.get(i)));
            if (i < (sortedKeys.size() - 1)) {
                carAssignGroup.setNextGroupCapacity(mappedGroups.get(sortedKeys.get(i + 1)).get(0).getCapacity());
            } else {
                double previousCapacity = mappedGroups.get(sortedKeys.get(i - 1)).get(0).getCapacity().getMaxVolume().getVolume();
                double currentCapacity = carAssignGroup.getCapacity().getMaxVolume().getVolume();
                carAssignGroup.setNextGroupCapacity(new Capacity(new Volume(2 * currentCapacity - previousCapacity)));
            }
            carGroups.add(carAssignGroup);
        }
    }

    private List<Capacity> getSortedKeys(Map<Capacity, List<Car>> mappedGroups) {
        return mappedGroups.keySet().stream()
                .sorted(Comparator.comparingDouble(c -> c.getMaxVolume().getVolume()))
                .collect(Collectors.toList());
    }

    public void resetGroupLevel() {
        currentGroupKey = 0;
        currentGroup = carGroups.get(currentGroupKey);
    }

    public Car getNextMostFreeCar() {
        return getNextMostFreeCar(true);
    }

    public Car getNextMostFreeCar(boolean refreshWights) {
        if (refreshWights)
            refreshGroupWeight();
        return currentGroup.getNextCar();
    }

    public void resetAssignCar(Car car) {
        CarAssignGroup carGroup = carGroups.stream()
                .filter(c -> c.getCapacity().equals(car.getCapacity()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Car is not contained in the assign list"));
        carGroup.resetAssignCar(car);
        car.resetFullness();
    }

    public Optional<Capacity> incrementGroupLevel() {
        if (currentGroupKey == carGroups.size() - 1)
            return Optional.empty();
        else {
            currentGroupKey++;
            currentGroup = carGroups.get(currentGroupKey);
            return Optional.of(currentGroup.getCapacity());
        }
    }

    private void refreshGroupWeight() {
        Optional<CarAssignGroup> minLoadedGroup = carGroups.stream().min(Comparator.comparingDouble(CarAssignGroup::getGroupLoad));
        if (minLoadedGroup.isPresent()) {
            CarAssignGroup group = minLoadedGroup.get();
            currentGroupKey = carGroups.indexOf(group);
            currentGroup = group;
        }
    }
}
