package com.nix.futuredelivery.distribution;

public class ProductDistributor {
    DistributionCell[][] distributionCells;
    DistributionParticipants distributionParticipants;


    public DistributionPlan distribute(){
        MinElementPlanSolver minElementPlanSolver = new MinElementPlanSolver(distributionCells,distributionParticipants);
        DistributionPlan firstDistributionPlan = minElementPlanSolver.findPlan();
        PotentialPlanSolver potentialPlanSolver = new PotentialPlanSolver(firstDistributionPlan,distributionParticipants);
        DistributionPlan optimalPlan = potentialPlanSolver.findOptimalPlan();
        return optimalPlan;
    }

    public ProductDistributor(DistributionCell[][] distributionCells, DistributionParticipants distributionParticipants) {
        this.distributionCells = distributionCells;
        this.distributionParticipants = distributionParticipants;
    }


}
