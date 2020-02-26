package com.nix.futuredelivery.distribution;

import java.util.ArrayList;
import java.util.List;

public class CycleMover {
    private DistributionPlan potentialPlan;
    private DistributionParticipants participants;

    private final double nullPlaceholder = -1111.221122;

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
        cell.fullness = nullPlaceholder;
        usedPositions = new ArrayList<>();
        if (checkColumnOnFilling(cell.y) > 0) {

            secondWay = makeCycle(cell, usedPositions, false);
        } else
            System.out.println("GLOBAL ERROR: CAN'T BUILD CYCLE!");
        cell.fullness = nullPlaceholder;

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
                    emptyCell.fullness = nullPlaceholder;
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
                    firstWay.get(i).fullness = nullPlaceholder;
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
                if (potentialPlan.plan[position.x][i].fullness != nullPlaceholder && potentialPlan.plan[position.x][i] != position) {
                    if (used.size() > 3 && used.get(0) == potentialPlan.plan[position.x][i]) {
                        used.add(potentialPlan.plan[position.x][i]);
                        return used;
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.plan[position.x][i]) && checkColumnOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            List<DistributionCell> recursiveRes = makeCycle(potentialPlan.plan[position.x][i], newArray, false);
                            if (recursiveRes != null) {
                                return recursiveRes;
                            }
                        }
                    }

                }
            }
        } else {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                if (potentialPlan.plan[i][position.y].fullness != nullPlaceholder && potentialPlan.plan[i][position.y] != position) {
                    if (used.size() > 3 && potentialPlan.plan[i][position.y] == used.get(0)) {
                        used.add(potentialPlan.plan[i][position.y]);
                        return used;
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.plan[i][position.y]) && checkRowOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            List<DistributionCell> recursiveRes = makeCycle(potentialPlan.plan[i][position.y], newArray, true);
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
        for (int i = 0; i < potentialPlan.plan[0].length; i++) {
            if (potentialPlan.plan[row][i].fullness != nullPlaceholder) {
                count++;
            }
        }
        return count;
    }

    private int checkColumnOnFilling(int column) {
        int count = 0;
        for (int i = 0; i < potentialPlan.plan.length; i++) {
            if (potentialPlan.plan[i][column].fullness != nullPlaceholder)
                count++;
        }
        return count;
    }

    private List<DistributionCell> findEmptyCells(DistributionCell minForbiddenCell){
        List<DistributionCell> resultList = new ArrayList<>();
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if(potentialPlan.plan[i][j]!=minForbiddenCell && potentialPlan.plan[i][j].fullness==nullPlaceholder)
                    resultList.add(potentialPlan.plan[i][j]);
            }
        }
        return resultList;
    }
}
