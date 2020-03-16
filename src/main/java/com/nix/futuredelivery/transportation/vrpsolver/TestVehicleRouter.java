package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Route;

import java.util.List;

public class TestVehicleRouter implements VehicleRoutingSolver {
    @Override
    public List<Route> setOrderInWaybills(List<Route> routeList, List<Distance> distances) {
        return routeList;
    }
}
