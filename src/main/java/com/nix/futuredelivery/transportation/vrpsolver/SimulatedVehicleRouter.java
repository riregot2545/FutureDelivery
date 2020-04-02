package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Route;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class which controls simulated vehicle
 */
@Slf4j
public class SimulatedVehicleRouter implements VehicleRoutingSolver {
    @Override
    public List<Route> setOrderInWaybills(List<Route> routeList, List<Distance> distances) {
        log.info("Starting routes optimization");
        for (int i = 0; i < routeList.size(); i++) {
            log.info("Processing route {}/{}", (i + 1), routeList.size());
            Route route = routeList.get(i);
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(route, distances);
            simulatedAnnealing.simulateAnnealing();
        }
        log.info("End route optimization");
        return routeList;
    }
}
