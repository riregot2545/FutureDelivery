package com.nix.futuredelivery.distribution;

import java.util.Arrays;

public class PotentialPlanSolver {
    private DistributionPlan distributionPlan;
    private DistributionParticipants participants;
    private CycleMover cycleMover;

    private double[] uArray;
    private double[] vArray;

    private final double nullPlaceholder = -1111.221122;

    public PotentialPlanSolver(DistributionPlan firstDistributionPlan, DistributionParticipants participants) {
        this.distributionPlan = firstDistributionPlan;
        this.participants = participants;
        this.cycleMover = new CycleMover(distributionPlan, participants);
    }

    public DistributionPlan findOptimalPlan() {
        DistributionCell maxPotentialCell;

        clearPotentialArray();

        makePotentials();
        maxPotentialCell = findMaxPotentialSum();

        do {

            System.out.println("Max potential before cycle:" + maxPotentialCell.potentialSum +
                    " on " + maxPotentialCell.x + "/" + maxPotentialCell.y);

            cycleMover.cycle(maxPotentialCell);

            makePotentials();
            maxPotentialCell = findMaxPotentialSum();
            System.out.println("Max potential after cycle:" + maxPotentialCell.potentialSum +
                    " on " + maxPotentialCell.x + "/" + maxPotentialCell.y);


            clearPotentialArray();
        }
        while (maxPotentialCell.potentialSum > 0);

        DistributionCell[][] optimalMatrix = new DistributionCell[participants.suppliersCount()][participants.consumersCount()];
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                optimalMatrix[i][j] =  new DistributionCell();
                optimalMatrix[i][j].fullness = distributionPlan.plan[i][j].fullness;
            }
        }
        return new DistributionPlan(optimalMatrix);
    }

    private void clearPotentialArray(){
        uArray = new double[participants.consumersCount()];
        vArray = new double[participants.suppliersCount()];

        Arrays.fill(uArray, nullPlaceholder);
        Arrays.fill(vArray, nullPlaceholder);
    }

    private void makePotentials(){
        uArray[0] = 0;
        int iterations = 0;
        while (findElementIndex(uArray, nullPlaceholder) > -1 || findElementIndex(vArray, nullPlaceholder) > -1) {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                for (int j = 0; j < participants.consumersCount(); j++) {
                    if (distributionPlan.plan[i][j].fullness != nullPlaceholder) {
                        if (uArray[j] != nullPlaceholder && vArray[i] == nullPlaceholder) {
                            vArray[i] = distributionPlan.plan[i][j].cost - uArray[j];
                        } else if (uArray[j] == nullPlaceholder && vArray[i] != nullPlaceholder) {
                            uArray[j] = distributionPlan.plan[i][j].cost - vArray[i];
                        }
                    }
                }
            }
            iterations++;
            if (iterations > 20_000) {
                int uElementNullIndex = findElementIndex(uArray, nullPlaceholder);
                int vElementNullIndex = findElementIndex(vArray, nullPlaceholder);
                if (uElementNullIndex > -1) {
                    uArray[uElementNullIndex] = 0;
                    continue;
                } else if (vElementNullIndex > -1) {
                    vArray[vElementNullIndex] = 0;
                }
            }
        }
    }

    private DistributionCell findMaxPotentialSum() {
        double maxElementSum = Integer.MIN_VALUE;

        DistributionCell maxCell = null;
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if (distributionPlan.plan[i][j].fullness == nullPlaceholder) {
                    distributionPlan.plan[i][j].potentialSum = uArray[j] + vArray[i] - distributionPlan.plan[i][j].cost;
                    if (distributionPlan.plan[i][j].potentialSum > maxElementSum) {
                        maxElementSum = distributionPlan.plan[i][j].potentialSum;
                        maxCell = distributionPlan.plan[i][j];
                    }
                }
            }
        }
        return maxCell;
    }

    private int findElementIndex(double[] mass, double element) {
        for (int i = 0; i < mass.length; i++) {
            if (mass[i] == element)
                return i;
        }
        return -1;
    }
}
