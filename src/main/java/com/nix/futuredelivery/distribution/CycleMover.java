package com.nix.futuredelivery.distribution;

import java.util.ArrayList;
import java.util.List;

public class CycleMover {
    private DistributionPlan potentialPlan;
    private DistributionParticipants participants;



    public CycleMover(DistributionPlan potentialPlan, DistributionParticipants participants) {
        this.potentialPlan = potentialPlan;
        this.participants = participants;
    }

    public DistributionPlan cycle(DistributionCell cell) {
        List<DistributionCell> usedPositions = new ArrayList<>();
        List<DistributionCell> firstWay = null;
        List<DistributionCell> secondWay = null;

        if (checkRowOnFilling(cell.x) > 0) {

            firstWay = makeCycle(cell, usedPositions, true);
        }
        cell.fullness = DistributionCell.EMPTY_FULLNESS_PLACEHOLDER;
        usedPositions = new ArrayList<>();
        if (checkColumnOnFilling(cell.y) > 0) {

            secondWay = makeCycle(cell, usedPositions, false);
        } else
            System.out.println("GLOBAL ERROR: CAN'T BUILD CYCLE!");
        cell.fullness = DistributionCell.EMPTY_FULLNESS_PLACEHOLDER;

        if (firstWay == null && secondWay == null) {
            System.out.println("GLOBAL ERROR: CAN'T BUILD CYCLE!");
            List<DistributionCell> tryFillCellList = findEmptyCells(cell);
            for (DistributionCell emptyCell :
                    tryFillCellList) {
                emptyCell.fullness = 0;
                firstWay = makeCycle(cell, usedPositions, true);
                usedPositions.clear();
                secondWay = makeCycle(cell, usedPositions, false);
                if (firstWay != null || secondWay != null)
                    break;
                else
                    emptyCell.fullness = DistributionCell.EMPTY_FULLNESS_PLACEHOLDER;
                usedPositions.clear();
            }
        }

        if ((firstWay == null && secondWay != null) || firstWay.size() > secondWay.size())
            firstWay = secondWay;

        DistributionCell minCost = firstWay.get(1);
        for (int i = 2; i < firstWay.size(); i++) {
            if (i % 2 != 0 && firstWay.get(i).fullness < minCost.fullness)
                minCost = firstWay.get(i);
        }
        cell.fullness = 0;
        double minCostValue = minCost.fullness;
        for (int i = 0; i < firstWay.size() - 1; i++) {
            if (i % 2 == 0) {
                firstWay.get(i).fullness += minCostValue;
            } else {
                firstWay.get(i).fullness -= minCostValue;
                if (firstWay.get(i).fullness == 0)
                    firstWay.get(i).fullness = DistributionCell.EMPTY_FULLNESS_PLACEHOLDER;
            }


        }

        return potentialPlan;
    }

    private List<DistributionCell> makeCycle(DistributionCell position, List<DistributionCell> used, boolean isRow) {
        used.add(position);
        if (used.size() == 1)
            used.get(0).fullness = 1;

        if (used.size() > 3 && used.get(used.size() - 1) == used.get(0))
            return used;
        if (isRow) {
            for (int i = 0; i < participants.consumersCount(); i++) {
                if (potentialPlan.getCell(position.x,i).fullness != DistributionCell.EMPTY_FULLNESS_PLACEHOLDER
                        && potentialPlan.getCell(position.x,i) != position) {
                    if (used.size() > 3 && used.get(0) == potentialPlan.getCell(position.x,i)) {
                        used.add(potentialPlan.getCell(position.x,i));
                        return used;
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(position.x,i)) && checkColumnOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            List<DistributionCell> recursiveRes = makeCycle(potentialPlan.getCell(position.x,i), newArray, false);
                            if (recursiveRes != null) {
                                return recursiveRes;
                            }
                        }
                    }

                }
            }
        } else {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                if (potentialPlan.getCell(i,position.y).fullness != DistributionCell.EMPTY_FULLNESS_PLACEHOLDER
                        && potentialPlan.getCell(i,position.y) != position) {
                    if (used.size() > 3 && potentialPlan.getCell(i,position.y) == used.get(0)) {
                        used.add(potentialPlan.getCell(i,position.y));
                        return used;
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(i,position.y)) && checkRowOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            List<DistributionCell> recursiveRes = makeCycle(potentialPlan.getCell(i,position.y), newArray, true);
                            if (recursiveRes != null) {
                                return recursiveRes;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private int checkRowOnFilling(int row) {
        int count = 0;
        for (int i = 0; i < potentialPlan.width; i++) {
            if (potentialPlan.getCell(row,i).fullness != DistributionCell.EMPTY_FULLNESS_PLACEHOLDER ) {
                count++;
            }
        }
        return count;
    }

    private int checkColumnOnFilling(int column) {
        int count = 0;
        for (int i = 0; i < potentialPlan.height; i++) {
            if (potentialPlan.getCell(i,column).fullness != DistributionCell.EMPTY_FULLNESS_PLACEHOLDER )
                count++;
        }
        return count;
    }

    private List<DistributionCell> findEmptyCells(DistributionCell minForbiddenCell){
        List<DistributionCell> resultList = new ArrayList<>();
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if(potentialPlan.getCell(i,j)!=minForbiddenCell && potentialPlan.getCell(i,j).fullness==DistributionCell.EMPTY_FULLNESS_PLACEHOLDER )
                    resultList.add(potentialPlan.getCell(i,j));
            }
        }
        return resultList;
    }
}
