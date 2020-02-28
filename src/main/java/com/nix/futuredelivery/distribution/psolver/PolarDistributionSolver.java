package com.nix.futuredelivery.distribution.psolver;

import com.nix.futuredelivery.distribution.DistributionEntry;
import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PolarDistributionSolver {
    private final List<Car> cars;
    private final Map<Warehouse, List<DistributionEntry>> mapedWarehouses;
    public void assign(){

    }
}
