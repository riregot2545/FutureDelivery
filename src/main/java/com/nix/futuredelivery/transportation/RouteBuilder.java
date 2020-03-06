package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.transportation.psolver.PolarDistributionSolver;
import com.nix.futuredelivery.transportation.vrpsolver.VehicleRoutingSolver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
//@Service
public class RouteBuilder {
    private final DistributionGrouper distributionGrouper;
    private final ProductDistributor productDistributor;
    private final PolarDistributionSolver polarSolver;
    private final VehicleRoutingSolver routingSolver;

//    private final
//
//    public void
}
