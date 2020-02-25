package com.nix.futuredelivery.distribution;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinElementPlanSolver {
    private DistributionCell[][] costMatrix;

    public MinElementPlanSolver(DistributionCell[][] costMatrix, DistributionParticipants participants) {
        this.costMatrix = Arrays.stream(costMatrix).map(DistributionCell[]::clone).toArray(e->costMatrix.clone());
        this.participants = participants;
    }

    private DistributionParticipants participants;

    public DistributionPlan findPlan(){
        throw new NotImplementedException();
    }
}
