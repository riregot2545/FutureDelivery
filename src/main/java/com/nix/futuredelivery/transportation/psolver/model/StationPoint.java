package com.nix.futuredelivery.transportation.psolver.model;

import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StationPoint {
    private final double angle;
    private final Store station;
    private final Warehouse base;
    private List<AssignOrderLine> productLines;

    private Volume allProductsVolume;

    public void setAllProductsVolume() {
        if (productLines != null && productLines.size() > 0)
            allProductsVolume = new Volume(productLines.stream()
                    .mapToDouble(e -> e.getProduct().getVolume().getVolumeWeight() * e.getQuantity())
                    .sum());
        else
            allProductsVolume = Volume.empty();
    }

    public boolean hasDemand() {
        return productLines.stream().anyMatch(line -> line.getRemainQuantity() > 0);
    }
}
