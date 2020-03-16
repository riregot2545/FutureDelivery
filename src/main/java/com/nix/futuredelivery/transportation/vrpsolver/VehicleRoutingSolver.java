package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Route;

import java.util.List;

public interface VehicleRoutingSolver {
    List<Route> setOrderInWaybills(List<Route> routeList, List<Distance> distances);
}
