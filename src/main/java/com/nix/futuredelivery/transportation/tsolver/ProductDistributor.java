package com.nix.futuredelivery.transportation.tsolver;

import com.nix.futuredelivery.transportation.model.exceptions.PotentialConflictException;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionCell;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionPlan;
import com.nix.futuredelivery.transportation.tsolver.model.MatrixPosition;

/**
 * Main class of transportation problem solving. It contain solving logic for
 * building distribution plans, that defining product amounts and route points.
 */
public class ProductDistributor {
    private final DistributionCell[][] distributionCells;
    private final DistributionParticipants distributionParticipants;

    /**
     * Constructs distributor from pre-defined distribution cell array and distribution participants.
     *
     * @param distributionCells        pre-defined distribution cell matrix, its height and width must be equal
     *                                 with participants consumer and supplier count.
     * @param distributionParticipants participants of distribution.
     */
    public ProductDistributor(DistributionCell[][] distributionCells, DistributionParticipants distributionParticipants) {
        this.distributionCells = distributionCells;
        this.distributionParticipants = distributionParticipants;
    }

    /**
     * Constructs distributor from double cost matrix and build cell matrix inside
     * @param costArray double matrix, its height and width must be equal with participants consumer and supplier count.
     * @param distributionParticipants participants of distribution.
     */
    public ProductDistributor(double[][] costArray, DistributionParticipants distributionParticipants) {
        this.distributionCells = new DistributionCell[distributionParticipants.suppliersCount()][distributionParticipants.consumersCount()];
        for (int i = 0; i < distributionParticipants.suppliersCount(); i++) {
            for (int j = 0; j < distributionParticipants.consumersCount(); j++) {
                distributionCells[i][j] = new DistributionCell(new MatrixPosition(i,j),costArray[i][j]);
            }
        }
        this.distributionParticipants = distributionParticipants;
    }

    /**
     * Entry point of transportation problem solver. First of all minimal element algorithm builds initial plan of distribution.
     * Then potential algorithm improves initial plan by cyclic moves.
     * @return optimal {@code DistributionPlan}.
     * @throws PotentialConflictException if potential conflicts in cycles cannot be resolved without shuffle of product distribution.
     */
    public DistributionPlan distribute() throws PotentialConflictException {
        MinElementPlanSolver minElementPlanSolver = new MinElementPlanSolver(distributionCells, distributionParticipants);
        DistributionPlan firstDistributionPlan = minElementPlanSolver.findPlan();
        PotentialPlanSolver potentialPlanSolver = new PotentialPlanSolver(firstDistributionPlan);
        DistributionPlan optimalPlan = potentialPlanSolver.findOptimalPlan();
        optimalPlan.clearEmptyFullness();
        return optimalPlan;
    }
}
