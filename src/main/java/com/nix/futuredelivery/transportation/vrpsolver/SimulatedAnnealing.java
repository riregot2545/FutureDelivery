package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    public double simulateAnnealing() {
        log.info("Building optimized distance for route: {}", route);
        log.info("Starting SA with temperature: {} , # of iterations: {} and colling rate: {}", startingTemperature, numberOfIterations, coolingRate);
        double temperature = startingTemperature;

        double bestDistance = travel.getDistance();
        log.debug("Initial distance of travel: {}", bestDistance);
        Travel currentSolution = travel;

        for (int i = 0; i < numberOfIterations; i++) {
            if (temperature > 0.1) {
                currentSolution.swapStations();
                double currentDistance = currentSolution.getDistance();
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                } else if (Math.exp((bestDistance - currentDistance) / temperature) < Math.random()) {
                    currentSolution.revertSwap();
                }
                temperature *= coolingRate;
            } else {
                break;
            }
            if (i % 100 == 0) {
                log.debug("Iteration #{}", i);
            }
        }
        return bestDistance;
    }

}