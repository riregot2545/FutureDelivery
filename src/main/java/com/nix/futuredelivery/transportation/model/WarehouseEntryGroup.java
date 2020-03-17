package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Transportation model class that implements {@code KeyListGroup} interface with warehouse as key
 * and {@code List<DistributionEntry>>} as list.
 */
@AllArgsConstructor
public class WarehouseEntryGroup implements KeyListGroup<Warehouse, List<DistributionEntry>> {
    private final Warehouse warehouse;
    private final List<DistributionEntry> list;

    @Override
    public Warehouse getKey() {
        return warehouse;
    }

    @Override
    public List<DistributionEntry> getList() {
        return list;
    }
}
