package com.nix.futuredelivery.distribution.tsolver.model;

import lombok.Getter;

public class DistributionPlan {
    @Getter
    private int height;
    @Getter
    private int width;

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
        this.plan = new DistributionCell[participants.suppliersCount()][participants.consumersCount()];
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j <participants.consumersCount(); j++) {
                plan[i][j] = new DistributionCell(new MatrixPosition(i,j),costArray[i][j]);
            }
        }
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
                costSum+= getCell(i,j).getCellCost();
            }
        }
        return costSum;
    }

    public void clearEmptyFullness(){
        for (DistributionCell[] distributionCells : plan) {
            for (int j = 0; j < plan[0].length; j++) {
                if (distributionCells[j].getFullness() == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER)
                    distributionCells[j].setFullness(0);
            }
        }
    }

    public static DistributionPlan fromCostFullnessArray(double[][] costArray, int[][] fullnessArray, DistributionParticipants participants){
        if(costArray.length != fullnessArray.length && costArray[0].length != fullnessArray[0].length)
            throw new IllegalArgumentException("Cost and fullness matrices must be with same size.");

        DistributionPlan plan = new DistributionPlan(costArray,participants);
        for (int i = 0; i < fullnessArray.length; i++) {
            for (int j = 0; j < fullnessArray[0].length; j++) {
                DistributionCell cell = plan.getCell(i, j);
                cell.setFullness(fullnessArray[i][j]);
            }
        }
        return plan;
    }
}
