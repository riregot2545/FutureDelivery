package com.nix.futuredelivery.transportation.vrpsolver;


import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Data
public class Travel {

    private List<Station> travelList;
    private List<Station> previousTravelList;

    private List<Distance> distances;

    private final Random random = new Random();

    public Travel(List<Store> stores, Warehouse warehouse, List<Distance> distances) {
        travelList.add(new Station(warehouse.getAddress(), true));
        for (Store store : stores) {
            travelList.add(new Station(store.getAddress(), false));
        }
        this.distances = distances;
    }


    public void swapStations() {
        int a = generateRandomIndex();
        int b = generateRandomIndex();
        previousTravelList = travelList;
        Station x = travelList.get(a);
        Station y = travelList.get(b);
        travelList.set(a, y);
        travelList.set(b, x);
    }

    public void revertSwap() {
        travelList = previousTravelList;
    }

    private int generateRandomIndex() {
        return random.nextInt(travelList.size());
    }

    private double getDistanceBetween(Station stationFrom, Station stationTo) {
        Optional<Distance> distance = distances.stream().filter(d -> d.getAddressFrom().equals(stationFrom.getAddress()) &&
                d.getAddressTo().equals(stationTo.getAddress())).findFirst();
        if (distance.isPresent())
            return distance.get().getDistance();
        else
            throw new IllegalStateException("Distance between station " + stationFrom + " and station " + stationTo + " not found.");
    }

    public Station getStation(int index) {
        return travelList.get(index);
    }

    public double getDistance() {
        double distance = 0;
        for (int i = 0; i < travelList.size(); i++) {
            Station starting = getStation(i);
            Station destination;
            if (i + 1 < travelList.size()) {
                destination = getStation(i + 1);
            } else {
                destination = getStation(0);
            }
            distance += getDistanceBetween(starting, destination);
        }
        return distance;
    }

}