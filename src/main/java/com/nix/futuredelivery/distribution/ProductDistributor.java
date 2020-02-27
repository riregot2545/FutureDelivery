package com.nix.futuredelivery.distribution;

import com.nix.futuredelivery.distribution.tsolver.MinElementPlanSolver;
import com.nix.futuredelivery.distribution.tsolver.PotentialPlanSolver;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionCell;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionPlan;
import com.nix.futuredelivery.distribution.tsolver.model.MatrixPosition;

public class ProductDistributor {
    private final DistributionCell[][] distributionCells;
    private final DistributionParticipants distributionParticipants;


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
        this.distributionCells = new DistributionCell[distributionParticipants.suppliersCount()][distributionParticipants.consumersCount()];
        for (int i = 0; i < distributionParticipants.suppliersCount(); i++) {
            for (int j = 0; j < distributionParticipants.consumersCount(); j++) {
                distributionCells[i][j] = new DistributionCell(new MatrixPosition(i,j),costArray[i][j]);
            }
        }
        this.distributionParticipants = distributionParticipants;
    }


}
