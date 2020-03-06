package com.nix.futuredelivery.distribution.psolver.model;

import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.Volume;
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
    private Volume satisfaction;

    public Volume getRemain() {
        return new Volume(allProductsVolume.getVolume() - satisfaction.getVolume());
    }

    public void addSatisfaction(Volume volume) {
        satisfaction.setVolume(satisfaction.getVolume() + volume.getVolume());
    }
}
