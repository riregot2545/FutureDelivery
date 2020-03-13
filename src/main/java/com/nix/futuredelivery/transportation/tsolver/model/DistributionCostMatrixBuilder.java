package com.nix.futuredelivery.transportation.tsolver.model;

import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class DistributionCostMatrixBuilder {

    private final DistributionParticipants participants;
    private final Map<Warehouse, List<Distance>> distances;

    public DistributionCell[][] build(){
        DistributionCell[][] cells = new DistributionCell[participants.suppliersCount()][participants.consumersCount()];
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if(participants.isConsumerFictive(j))
                    cells[i][j] = new DistributionCell(new MatrixPosition(i,j),0D);
                else {
                    int finalJ = j;
                    Optional<Distance> distance = distances.get(participants.getSupplierWarehouse(i))
                            .stream()
                            .filter(d -> d.getAddressTo().equals(participants.getConsumerAddress(finalJ)))
                            .findFirst();
                    if (distance.isPresent()) {
                        cells[i][j] = new DistributionCell(new MatrixPosition(i, j), distance.get().getDistance());
                    } else {
                        throw new IllegalStateException("Distance from " + participants.getSupplierAddress(i) + " to " +
                                participants.getConsumerAddress(j) + " is not exist in db.");
                    }
                }
            }
        }
        return cells;
    }
}
