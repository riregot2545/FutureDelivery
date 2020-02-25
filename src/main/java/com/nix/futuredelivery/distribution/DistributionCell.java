package com.nix.futuredelivery.distribution;

public class DistributionCell {
    public int x;
    public int y;

    public double cost;
    public double fullness;
    public double potentialSum;

    public DistributionCell() {
    }

    public DistributionCell(double cost) {
        this.cost = cost;
    }


}
