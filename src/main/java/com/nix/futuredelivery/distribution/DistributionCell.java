package com.nix.futuredelivery.distribution;

public class DistributionCell {
    public int x;
    public int y;

    public double cost;
    public int fullness;
    public double potentialSum;


    public static final int EMPTY_FULLNESS_PLACEHOLDER = -1;

    public DistributionCell() {
    }

    public DistributionCell(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return fullness == EMPTY_FULLNESS_PLACEHOLDER ? 0 : cost * fullness;
    }


}
