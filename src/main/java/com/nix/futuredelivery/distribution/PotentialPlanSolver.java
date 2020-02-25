package com.nix.futuredelivery.distribution;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PotentialPlanSolver {
    private DistributionPlan distributionPlan;
    private DistributionParticipants participants;
    private CycleMover cycleMover;



    public PotentialPlanSolver(DistributionPlan firstDistributionPlan, DistributionParticipants participants) {
        this.distributionPlan = firstDistributionPlan;
        this.participants = participants;
        this.cycleMover = new CycleMover(distributionPlan, participants);
    }

    public DistributionPlan findOptimalPlan() {
        throw new NotImplementedException();
    }
}
