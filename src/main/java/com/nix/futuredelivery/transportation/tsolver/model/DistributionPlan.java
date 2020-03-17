package com.nix.futuredelivery.transportation.tsolver.model;

import lombok.Getter;

/**
 * Transportation solver model class that represents aggregation of distribution cells
 * for data processing.
 */
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


    /**
     * Constructs new instance from pre-defined distribution cell matrix and participants.
     *
     * @param cells        its height and width must be equal
     *                     with participants consumer and supplier count.
     * @param participants participants of distribution.
     */
    public DistributionPlan(DistributionCell[][] cells, DistributionParticipants participants) {
        this.plan = cells;
        this.height = participants.suppliersCount();
        this.width = participants.consumersCount();
        this.participants = participants;
    }

    /**
     * Constructs new instance from double matrix and participants.
     * @param costArray double matrix, which height and width must be equal
     *              with participants consumer and supplier count.
     * @param participants participants of distribution.
     */
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

    /**
     * Gets cell by matrix coordinates. May throw {@code IllegalArgumentException} if i or j out of bounds.
     * @param i x (height) coordinate of matrix.
     * @param j y (width) coordinate of matrix.
     * @return the cell the at the specified position in this plan
     */
    public DistributionCell getCell(int i, int j) {
        if (i < 0 || j < 0 || i >= height || j >= width)
            throw new IllegalArgumentException("Array index must be positive integer and be in bounds of height and width of plan");
        return plan[i][j];
    }

    /**
     * Returns sum of all cell costs.
     * @return all cell in plan cost or zero.
     */
    public double totalCost(){
        double costSum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                costSum+= getCell(i,j).getCellCost();
            }
        }
        return costSum;
    }

    /**
     * Clears fullness with empty placeholder to zero.
     */
    public void clearEmptyFullness(){
        for (DistributionCell[] distributionCells : plan) {
            for (int j = 0; j < plan[0].length; j++) {
                if (distributionCells[j].getFullness() == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER)
                    distributionCells[j].setFullness(0);
            }
        }
    }

    /**
     * Static method that build new plan by combination cost and fullness matrix. All matrix sizes and participants must be equal,
     * or {@code IllegalArgumentException} will be thrown.
     * @param costArray double cost matrix.
     * @param fullnessArray int fullness matrix.
     * @param participants distribution participants.
     * @return new instance of {@code DistributionPlan}.
     */
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
