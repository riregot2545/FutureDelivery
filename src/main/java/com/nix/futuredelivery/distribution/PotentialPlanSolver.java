package com.nix.futuredelivery.distribution;

public class PotentialPlanSolver {
    private DistributionPlan distributionPlan;
    private DistributionParticipants participants;
    private CycleMover cycleMover;

    private PotentialArray uArray;
    private PotentialArray vArray;

    public PotentialPlanSolver(DistributionPlan firstDistributionPlan) {
        this.distributionPlan = firstDistributionPlan;
        this.participants = firstDistributionPlan.getParticipants();
        this.cycleMover = new CycleMover(distributionPlan, participants);

        this.uArray = new PotentialArray(participants.consumersCount());
        this.vArray = new PotentialArray(participants.suppliersCount());
    }

    public DistributionPlan findOptimalPlan() {
        makePotentials();
        DistributionCell maxPotentialCell = findMaxPotentialSum();
        do {

            System.out.println("Max potential before cycle:" + maxPotentialCell.potentialSum +
                    " on " + maxPotentialCell.x + "/" + maxPotentialCell.y);


            cycleMover.cycle(maxPotentialCell);

            makePotentials();
            maxPotentialCell = findMaxPotentialSum();
            System.out.println("Max potential after cycle:" + maxPotentialCell.potentialSum +
                    " on " + maxPotentialCell.x + "/" + maxPotentialCell.y);

            uArray.clear();
            vArray.clear();
        }
        while (maxPotentialCell.potentialSum > 0);

        return distributionPlan;
    }


    private void makePotentials() {
        uArray.set(0, 0);
        int iterations = 0;
        while (uArray.findIndexOfNull() > -1 || vArray.findIndexOfNull() > -1) {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                for (int j = 0; j < participants.consumersCount(); j++) {
                    if (distributionPlan.getCell(i,j).fullness != -1) {
                        if (!uArray.isNull(j) && vArray.isNull(i)) {
                            vArray.set(i, distributionPlan.getCell(i,j).cost - uArray.get(j));
                        } else if (uArray.isNull(j) && !vArray.isNull(i)) {
                            uArray.set(j, distributionPlan.getCell(i,j).cost - vArray.get(i));
                        }
                    }
                }
            }
            iterations++;
            if (iterations > 20_000) {
                System.out.println("GLOBAL WARNING ITERATIONS > 20 000, resolve potentials conflict...");
                int uElementNullIndex = uArray.findIndexOfNull();
                int vElementNullIndex = vArray.findIndexOfNull();
                if (uElementNullIndex > -1) {
                    uArray.set(uElementNullIndex, 0);
                    continue;
                } else if (vElementNullIndex > -1) {
                    vArray.set(vElementNullIndex, 0);
                }
            }
        }
    }

    private DistributionCell findMaxPotentialSum() {
        double maxElementSum = Integer.MIN_VALUE;

        DistributionCell maxCell = null;
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if (distributionPlan.getCell(i,j).fullness == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER) {
                    distributionPlan.getCell(i,j).potentialSum = uArray.get(j) + vArray.get(i) - distributionPlan.getCell(i,j).cost;
                    if (distributionPlan.getCell(i,j).potentialSum > maxElementSum) {
                        maxElementSum = distributionPlan.getCell(i,j).potentialSum;
                        maxCell = distributionPlan.getCell(i,j);
                    }
                }
            }
        }
        return maxCell;
    }
}
