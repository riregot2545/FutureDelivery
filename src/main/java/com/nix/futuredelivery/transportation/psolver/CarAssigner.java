package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;

import java.util.*;
import java.util.stream.Collectors;

public class CarAssigner {
    private final List<AssignCarGroup> carGroups;
    private AssignCarGroup currentGroup;
    private int currentGroupKey;

    public CarAssigner(List<Car> carList) {
        if (carList.isEmpty())
            throw new IllegalArgumentException("Car list is empty");
        Map<Capacity, List<Car>> mappedGroups = carList.stream()
                .collect(Collectors.groupingBy(Car::getCapacity));

        carGroups = new ArrayList<>();

        List<Capacity> sortedKeys = mappedGroups.keySet().stream()
                .sorted(Comparator.comparingDouble(c -> c.getMaxVolume().getVolume()))
                .collect(Collectors.toList());
        for (int i = 0; i < sortedKeys.size(); i++) {
            AssignCarGroup assignCarGroup = new AssignCarGroup(mappedGroups.get(sortedKeys.get(i)));
            if (i < (sortedKeys.size() - 1)) {
                assignCarGroup.setNextGroupCapacity(mappedGroups.get(sortedKeys.get(i + 1)).get(0).getCapacity());
            } else {
                double previousCapacity = mappedGroups.get(sortedKeys.get(i - 1)).get(0).getCapacity().getMaxVolume().getVolume();
                double currentCapacity = assignCarGroup.getCapacity().getMaxVolume().getVolume();
                assignCarGroup.setNextGroupCapacity(new Capacity(new Volume(2 * currentCapacity - previousCapacity)));
            }
            carGroups.add(assignCarGroup);
        }
        currentGroupKey = 0;
        currentGroup = carGroups.get(currentGroupKey);
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
        AssignCarGroup carGroup = carGroups.stream()
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
        Optional<AssignCarGroup> minLoadedGroup = carGroups.stream().min(Comparator.comparingDouble(AssignCarGroup::getGroupLoad));
        if (minLoadedGroup.isPresent()) {
            AssignCarGroup group = minLoadedGroup.get();
            currentGroupKey = carGroups.indexOf(group);
            currentGroup = group;
        }
    }
}
