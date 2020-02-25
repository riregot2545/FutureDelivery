package com.nix.futuredelivery.distribution;

public class DistributionPlan {
    public int height;
    public int width;

    private DistributionCell[][] plan;

    public DistributionPlan(DistributionCell[][] plan) {
        this.plan = plan;
        this.height = plan.length;
        this.width = plan[0].length;
    }

}
