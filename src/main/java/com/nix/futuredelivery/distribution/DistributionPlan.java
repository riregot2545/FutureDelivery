package com.nix.futuredelivery.distribution;

import lombok.Data;

import java.util.Arrays;

public class DistributionPlan {
    public int height;
    public int width;

    private DistributionCell[][] plan;

    private DistributionParticipants participants;
    public DistributionParticipants getParticipants() {
        return participants;
    }



    public DistributionPlan(DistributionCell[][] cells, DistributionParticipants participants) {
        this.plan = cells;
        this.height = participants.suppliersCount();
        this.width = participants.consumersCount();
        this.participants = participants;
    }

    public DistributionPlan(double[][] costArray, DistributionParticipants participants) {
        DistributionCell[][] cells = Arrays.stream(costArray)
                .map(i -> Arrays.stream(i)
                        .mapToObj(DistributionCell::new)
                        .toArray(DistributionCell[]::new))
                .toArray(DistributionCell[][]::new);
        this.plan = cells;
        this.height = participants.suppliersCount();
        this.width = participants.consumersCount();
    }

    public DistributionCell getCell(int i, int j){
        if(i<0 || j<0)
            throw new IllegalArgumentException("Array index must be positive integer");
        return plan[i][j];
    }

    public double totalCost(){
        double costSum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                costSum+= getCell(i,j).getCost();
            }
        }
        return costSum;
    }

    public void clearEmptyFullness(){
        for (int i = 0; i < plan.length; i++) {
            for (int j = 0; j < plan[0].length; j++) {
                if(plan[i][j].fullness == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER)
                    plan[i][j].fullness = 0;
            }
        }
    }

    public static DistributionPlan fromCostFullnessArray(double[][] costArray, int[][] fullnessArray, DistributionParticipants participants){
        if(costArray.length != fullnessArray.length && costArray[0].length != fullnessArray[0].length)
            throw new IllegalArgumentException("Cost and fullness matrices must be with same size.");

        DistributionPlan plan = new DistributionPlan(costArray,participants);
        for (int i = 0; i < fullnessArray.length; i++) {
            for (int j = 0; j < fullnessArray[0].length; j++) {
                plan.getCell(i,j).fullness = fullnessArray[i][j];
            }
        }
        return plan;
    }
}
