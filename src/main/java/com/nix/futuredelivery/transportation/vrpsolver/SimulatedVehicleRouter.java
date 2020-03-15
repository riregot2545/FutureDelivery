package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Route;

import java.util.List;

public class SimulatedVehicleRouter implements VehicleRoutingSolver {
    @Override
    public List<Route> setOrderInWaybills(List<Route> routeList, List<Distance> distances) {
        for (Route route : routeList) {
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(route, distances);
            double v = simulatedAnnealing.simulateAnnealing();
        }
        return routeList;
    }
}
