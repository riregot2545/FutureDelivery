package com.nix.futuredelivery.transportation.tsolver;


import com.nix.futuredelivery.transportation.tsolver.model.DistributionCell;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinElementPlanSolver {
    private DistributionCell[][] costMatrix;

    public MinElementPlanSolver(DistributionCell[][] costMatrix, DistributionParticipants participants) {
        this.costMatrix = Arrays.stream(costMatrix).map(DistributionCell[]::clone).toArray(e -> costMatrix.clone());
        this.participants = participants.clone();
    }

    private DistributionParticipants participants;

    public DistributionPlan findPlan() {
        DistributionParticipants originalParticipants = participants.clone();
        List<DistributionCell> cells = new ArrayList<>();
        for (int i = 0; i < costMatrix.length; i++) {
            cells.addAll(Arrays.asList(costMatrix[i]));
        }

        while (cells.size() > 0) {
            DistributionCell cell = findLowCostCell(cells);
            if (participants.getSupplierSupply(cell.getX()) > 0 && participants.getConsumerDemand(cell.getY()) > 0) {
                if (participants.getConsumerDemand(cell.getY()) < participants.getSupplierSupply(cell.getX())) {
                    subtractAllSupply(cell);
                } else {
                    subtractAllConsume(cell);
                }
            }
            cells.remove(cell);
        }

        fillEmptyFullnessByPlaceholder();

        return new DistributionPlan(costMatrix, originalParticipants);
    }

    private void subtractAllSupply(DistributionCell cell) {
        cell.setFullness(participants.getConsumerDemand(cell.getY()));
        participants.setSupplierSupply(cell.getX(),
                participants.getSupplierSupply(cell.getX()) - participants.getConsumerDemand(cell.getY()));
        participants.setConsumerDemand(cell.getY(), 0);
    }

    private void subtractAllConsume(DistributionCell cell) {
        cell.setFullness(participants.getSupplierSupply(cell.getX()));
        participants.setConsumerDemand(cell.getY(),
                participants.getConsumerDemand(cell.getY()) - participants.getSupplierSupply(cell.getX()));
        participants.setSupplierSupply(cell.getX(), 0);
    }

    private DistributionCell findLowCostCell(List<DistributionCell> list) {
        DistributionCell minCell = list.get(0);
        for (DistributionCell distributionCell : list) {
            if (distributionCell.getTariffCost() < minCell.getTariffCost()) {
                minCell = distributionCell;
            }
        }
        return minCell;
    }

    private void fillEmptyFullnessByPlaceholder() {
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if (costMatrix[i][j].getFullness() == 0)
                    costMatrix[i][j].setFullnessEmpty();
            }
        }
    }
}
