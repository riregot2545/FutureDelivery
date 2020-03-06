package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.transportation.psolver.PolarDistributionSolver;
import com.nix.futuredelivery.transportation.vrpsolver.VehicleRoutingSolver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
//@Service
public class TransportationOrderResolver {
    private final TransportationGrouper transportationGrouper;
    private final PolarDistributionSolver polarSolver;
    private final VehicleRoutingSolver routingSolver;

//    private final
//
//    public void
}
