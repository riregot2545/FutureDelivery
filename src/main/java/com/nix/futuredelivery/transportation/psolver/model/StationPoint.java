package com.nix.futuredelivery.transportation.psolver.model;

import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Polar solver model class used as wrapper of {@code Store}, that containing properties for polar calculations
 */
@Data
@AllArgsConstructor
public class StationPoint {
    private final double angle;
    private final Store station;
    private final Warehouse base;
    private List<AssignOrderLine> productLines;

    public boolean hasDemand() {
        return productLines.stream().anyMatch(line -> line.getRemainQuantity() > 0);
    }
}
