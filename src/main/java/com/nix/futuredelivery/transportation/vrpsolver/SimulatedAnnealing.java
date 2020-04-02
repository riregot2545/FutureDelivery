package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service class which uses the SA algorithm to calculate routes
 */
@Slf4j
public class SimulatedAnnealing {
    private final Route route;
    private final Warehouse warehouse;
    private final List<Store> stores;

    private Travel travel;


    public SimulatedAnnealing(Route route, List<Distance> distances) {
        this.route = route;
        this.warehouse = route.getWarehouse();
        this.stores = route.getRoutePoints();
        this.travel = new Travel(stores, warehouse, distances);
    }

    private final double startingTemperature = 10;
    private final int numberOfIterations = 100000;
    private final double coolingRate = 0.9995;

    public Route simulateAnnealing() {
        log.debug("Building optimized distance for route: {}, {}, {}", route.getWarehouse().getName(), route.getCar().getModel(), route.getDriver().getFirstName());
        log.debug("Starting SA with temperature: {} , # of iterations: {} and colling rate: {}", startingTemperature, numberOfIterations, coolingRate);
        double temperature = startingTemperature;

        double bestDistance = travel.getDistance();
        log.debug("Initial distance of travel: {}", bestDistance);

        for (int i = 0; i < numberOfIterations; i++) {
            if (temperature > 0.1) {
                travel.swapStations();
                double currentDistance = travel.getDistance();
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                } else if (Math.exp((bestDistance - currentDistance) / temperature) < Math.random()) {
                    travel.revertSwap();
                }
                temperature *= coolingRate;
            } else {
                break;
            }
            if (i % 100 == 0) {
                log.debug("Iteration #{}", i);
            }
        }
        log.debug("Optimized route built with distance {}", bestDistance);
        return setTravelOrder();
    }

    private Route setTravelOrder() {
        List<Station> travelList = moveListToWarehouseFirst(travel.getTravelList());

        travelList = travelList.subList(1, travelList.size());

        Map<Store, List<Waybill>> waybillsByStore = route.getWaybillList()
                .stream()
                .collect(Collectors.groupingBy(w -> w.getStoreOrder().getStore()));
        for (int i = 0; i < travelList.size(); i++) {
            Station station = travelList.get(i);
            Optional<Store> optionalStore = waybillsByStore.keySet().stream().filter(store -> store.getAddress().equals(station.getAddress())).findFirst();
            if (optionalStore.isPresent()) {
                int finalI = i;
                waybillsByStore.get(optionalStore.get()).forEach(w -> w.setDeliveryQueuePlace(finalI));
            } else {
                throw new IllegalStateException("Can't find station with address " + station.getAddress() + " in stores.");
            }
        }

        List<Store> stores = route.getWaybillList()
                .stream()
                .sorted(Comparator.comparingInt(Waybill::getDeliveryQueuePlace))
                .map(w -> w.getStoreOrder().getStore())
                .distinct()
                .collect(Collectors.toList());
        route.setRoutePoints(stores);

        return route;
    }

    private List<Station> moveListToWarehouseFirst(List<Station> stations) {
        int moveCount = IntStream.range(0, stations.size())
                .filter(i -> stations.get(i).isWarehouse())
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Stations is not contain warehouse."));

        for (int i = 0; i < moveCount; i++) {
            Station tmp = stations.get(0);
            for (int j = 0; j < stations.size() - 1; j++)
                stations.set(j, stations.get(j + 1));
            stations.set(stations.size() - 1, tmp);
        }

        return stations;
    }

}