package com.nix.futuredelivery.distribution.tsolver;

import com.nix.futuredelivery.distribution.tsolver.model.DistributionCell;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionPlan;
import com.nix.futuredelivery.distribution.tsolver.model.PotentialArray;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        if(isStartPlanOneColumnOrOneRow())
            return distributionPlan;

        makePotentials();
        DistributionCell maxPotentialCell = findMaxPotentialSum();
        while (maxPotentialCell.getPotentialSum() > 0) {

            log.info("Max potential before cycle:" + maxPotentialCell.getPotentialSum() +
                    " on [" + maxPotentialCell.getX()+ "," + maxPotentialCell.getY()+"]");


            cycleMover.cycle(maxPotentialCell);

            makePotentials();
            maxPotentialCell = findMaxPotentialSum();

            log.info("Max potential after cycle:" + maxPotentialCell.getPotentialSum() +
                    " on [" + maxPotentialCell.getX()+ "," + maxPotentialCell.getY()+"]");

            uArray.clear();
            vArray.clear();
        }


        return distributionPlan;
    }

    private boolean isStartPlanOneColumnOrOneRow() {
        return distributionPlan.getHeight()==1 || distributionPlan.getWidth() == 1;
    }


    private void makePotentials() {
        uArray.set(0, 0);
        int iterations = 0;
        while (uArray.findIndexOfNull() > -1 || vArray.findIndexOfNull() > -1) {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                for (int j = 0; j < participants.consumersCount(); j++) {
                    if (!distributionPlan.getCell(i,j).isFullnessNull()) {
                        if (!uArray.isNull(j) && vArray.isNull(i)) {
                            vArray.set(i, distributionPlan.getCell(i,j).getTariffCost() - uArray.get(j));
                        } else if (uArray.isNull(j) && !vArray.isNull(i)) {
                            uArray.set(j, distributionPlan.getCell(i,j).getTariffCost() - vArray.get(i));
                        }
                    }
                }
            }
            iterations++;
            if (iterations > 20_000) {
                log.warn("ITERATIONS > 20 000, maybe it is potentials conflict, resolving...");
                int uElementNullIndex = uArray.findIndexOfNull();
                int vElementNullIndex = vArray.findIndexOfNull();
                if (uElementNullIndex > -1) {
                    uArray.set(uElementNullIndex, 0);
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
                if (distributionPlan.getCell(i,j).isFullnessNull()) {
                    distributionPlan.getCell(i,j).setPotentialSum(
                            uArray.get(j) + vArray.get(i) - distributionPlan.getCell(i,j).getTariffCost());
                    if (distributionPlan.getCell(i,j).getPotentialSum() > maxElementSum) {
                        maxElementSum = distributionPlan.getCell(i,j).getPotentialSum();
                        maxCell = distributionPlan.getCell(i,j);
                    }
                }
            }
        }
        return maxCell;
    }
}
