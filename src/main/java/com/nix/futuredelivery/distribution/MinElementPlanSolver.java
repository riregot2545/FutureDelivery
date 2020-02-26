package com.nix.futuredelivery.distribution;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinElementPlanSolver {
    private DistributionCell[][] costMatrix;

    public MinElementPlanSolver(DistributionCell[][] costMatrix, DistributionParticipants participants) {
        this.costMatrix = Arrays.stream(costMatrix).map(DistributionCell[]::clone).toArray(e->costMatrix.clone());
        this.participants = participants;
    }

    private DistributionParticipants participants;

    public DistributionPlan findPlan(){
        List<DistributionCell> cells = new ArrayList<>();
        for (int i = 0; i < costMatrix.length; i++) {
            cells.addAll(Arrays.asList(costMatrix[i]));
        }

        while(cells.size()>0){
            DistributionCell cell = findLowCostCell(cells);
            if(participants.suppliers[cell.x].supply>0 && participants.consumers[cell.y].demand>0){
                if (participants.consumers[cell.y].demand < participants.suppliers[cell.x].supply) {
                    cell.fullness = participants.consumers[cell.y].demand;
                    participants.suppliers[cell.x].supply -= participants.consumers[cell.y].demand;
                    participants.consumers[cell.y].demand = 0;
                } else {
                    cell.fullness = participants.suppliers[cell.x].supply;
                    participants.consumers[cell.y].demand -= participants.suppliers[cell.x].supply;
                    participants.suppliers[cell.x].supply = 0;
                }
            }
            cells.remove(cell);
        }

        fillEmptyFullnessByPlaceholder();

        return new DistributionPlan(costMatrix,participants);
    }

    private DistributionCell findLowCostCell(List<DistributionCell> list) {
        DistributionCell minCell = list.get(0);
        for (DistributionCell distributionCell : list) {
            if (distributionCell.cost < minCell.cost) {
                minCell = distributionCell;
            }
        }
        return minCell;
    }

    private void fillEmptyFullnessByPlaceholder(){
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if(costMatrix[i][j].fullness==0)
                    costMatrix[i][j].fullness = DistributionCell.EMPTY_FULLNESS_PLACEHOLDER;
            }
        }
    }
}
