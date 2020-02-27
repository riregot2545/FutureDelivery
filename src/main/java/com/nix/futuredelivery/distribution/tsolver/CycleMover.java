package com.nix.futuredelivery.distribution.tsolver;

import com.nix.futuredelivery.distribution.tsolver.model.DistributionCell;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionPlan;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CycleMover {
    private DistributionPlan potentialPlan;
    private DistributionParticipants participants;


    public CycleMover(DistributionPlan potentialPlan, DistributionParticipants participants) {
        this.potentialPlan = potentialPlan;
        this.participants = participants;
    }

    public DistributionPlan cycle(DistributionCell cell) {
        List<DistributionCell> usedPositions = new ArrayList<>();
        Optional<List<DistributionCell>> firstWay = Optional.empty();
        Optional<List<DistributionCell>> secondWay = Optional.empty();

        if (checkRowOnFilling(cell.getX()) > 0) {
            firstWay = makeCycle(cell, usedPositions, true);
        }
        cell.setFullnessEmpty();
        usedPositions = new ArrayList<>();
        if (checkColumnOnFilling(cell.getY()) > 0) {
            secondWay = makeCycle(cell, usedPositions, false);
        } else
            log.warn("CAN'T BUILD CYCLE");
        cell.setFullnessEmpty();

        if (!firstWay.isPresent() && !secondWay.isPresent()) {
            log.warn("CAN'T BUILD CYCLE");
            log.warn("ADDING FICTIVE CELL TO BASIS");
            firstWay = addBasisCellAndRebuildCycle(usedPositions, cell);
        }


        if ((!firstWay.isPresent() && secondWay.isPresent()))
            firstWay = secondWay;
        else if(firstWay.isPresent() && secondWay.isPresent() && firstWay.get().size() > secondWay.get().size())
            firstWay = secondWay;

        firstWay.ifPresent(way-> {
            moveProductsUsingCycle(way, cell);
        });

        return potentialPlan;
    }

    private Optional<List<DistributionCell>> makeCycle(DistributionCell position, List<DistributionCell> used, boolean isRow) {
        used.add(position);
        if (used.size() == 1)
            used.get(0).setFullness(1);

        if (used.size() > 3 && used.get(used.size() - 1) == used.get(0))
            return Optional.of(used);
        if (isRow) {
            for (int i = 0; i < participants.consumersCount(); i++) {
                if (!potentialPlan.getCell(position.getX(), i).isFullnessEmpty()
                        && potentialPlan.getCell(position.getX(), i) != position) {
                    if (used.size() > 3 && used.get(0) == potentialPlan.getCell(position.getX(), i)) {
                        used.add(potentialPlan.getCell(position.getX(), i));
                        return Optional.of(used);
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(position.getX(), i))
                                && checkColumnOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            Optional<List<DistributionCell>> recursiveRes =
                                    makeCycle(potentialPlan.getCell(position.getX(), i), newArray, false);
                            if (recursiveRes.isPresent()) {
                                return recursiveRes;
                            }
                        }
                    }

                }
            }
        } else {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                if (!potentialPlan.getCell(i, position.getY()).isFullnessEmpty()
                        && potentialPlan.getCell(i, position.getY()) != position) {
                    if (used.size() > 3 && potentialPlan.getCell(i, position.getY()) == used.get(0)) {
                        used.add(potentialPlan.getCell(i, position.getY()));
                        return Optional.of(used);
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(i, position.getY()))
                                && checkRowOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            Optional<List<DistributionCell>> recursiveRes =
                                    makeCycle(potentialPlan.getCell(i, position.getY()), newArray, true);
                            if (recursiveRes.isPresent()) {
                                return recursiveRes;
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<List<DistributionCell>> addBasisCellAndRebuildCycle(List<DistributionCell> usedPositions,
                                             DistributionCell cell) {
        List<DistributionCell> tryFillCellList = findEmptyCells(cell);
        for (DistributionCell emptyCell :
                tryFillCellList) {
            emptyCell.setFullness(0);
            Optional<List<DistributionCell>> fw = makeCycle(cell, usedPositions, true);
            usedPositions.clear();
            Optional<List<DistributionCell>> sw = makeCycle(cell, usedPositions, false);
            if (fw.isPresent() || sw.isPresent())
            {
                if(fw.isPresent()){
                    return fw;
                }else{
                    return sw;
                }
            }
            else
                emptyCell.setFullnessEmpty();
            usedPositions.clear();
        }
        return Optional.empty();
    }

    private void moveProductsUsingCycle(List<DistributionCell> way,DistributionCell cell) {
        DistributionCell minCost = way.get(1);
        for (int i = 2; i < way.size(); i++) {
            if (i % 2 != 0 && way.get(i).getFullness() < minCost.getFullness())
                minCost = way.get(i);
        }
        cell.setFullness(0);
        int minCostValue = minCost.getFullness();
        for (int i = 0; i < way.size() - 1; i++) {
            if (i % 2 == 0) {
                way.get(i).setFullness(way.get(i).getFullness() + minCostValue);
            } else {
                way.get(i).setFullness(way.get(i).getFullness() - minCostValue);
                if (way.get(i).getFullness() == 0)
                    way.get(i).setFullnessEmpty();
            }
        }
    }

    private int checkRowOnFilling(int row) {
        int count = 0;
        for (int i = 0; i < potentialPlan.width; i++) {
            if (!potentialPlan.getCell(row, i).isFullnessEmpty()) {
                count++;
            }
        }
        return count;
    }

    private int checkColumnOnFilling(int column) {
        int count = 0;
        for (int i = 0; i < potentialPlan.height; i++) {
            if (!potentialPlan.getCell(i, column).isFullnessEmpty())
                count++;
        }
        return count;
    }

    private List<DistributionCell> findEmptyCells(DistributionCell minForbiddenCell) {
        List<DistributionCell> resultList = new ArrayList<>();
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if (potentialPlan.getCell(i, j) != minForbiddenCell && potentialPlan.getCell(i, j).isFullnessEmpty())
                    resultList.add(potentialPlan.getCell(i, j));
            }
        }
        return resultList;
    }
}
