package com.nix.futuredelivery.distribution;

import java.util.Arrays;

public class ProductDistributor {
    DistributionCell[][] distributionCells;
    DistributionParticipants distributionParticipants;


    public DistributionPlan distribute() {
        MinElementPlanSolver minElementPlanSolver = new MinElementPlanSolver(distributionCells, distributionParticipants);
        DistributionPlan firstDistributionPlan = minElementPlanSolver.findPlan();
        PotentialPlanSolver potentialPlanSolver = new PotentialPlanSolver(firstDistributionPlan);
        DistributionPlan optimalPlan = potentialPlanSolver.findOptimalPlan();
        optimalPlan.clearEmptyFullness();
        return optimalPlan;
    }

    public ProductDistributor(DistributionCell[][] distributionCells, DistributionParticipants distributionParticipants) {
        this.distributionCells = distributionCells;
        this.distributionParticipants = distributionParticipants;
    }

    public ProductDistributor(double[][] costArray, DistributionParticipants distributionParticipants) {
        this.distributionCells = Arrays.stream(costArray)
                .map(i -> Arrays.stream(i)
                        .mapToObj(DistributionCell::new)
                        .toArray(DistributionCell[]::new))
                .toArray(DistributionCell[][]::new);
        for (int i = 0; i < distributionParticipants.suppliersCount(); i++) {
            for (int j = 0; j < distributionParticipants.consumersCount(); j++) {
                distributionCells[i][j].x = i;
                distributionCells[i][j].y = j;
            }
        }

        this.distributionParticipants = distributionParticipants;
    }


}
