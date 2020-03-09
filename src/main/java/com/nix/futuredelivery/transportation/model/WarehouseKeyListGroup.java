package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class WarehouseKeyListGroup implements KeyListGroup<Warehouse, List<DistributionEntry>> {
    private final Warehouse warehouse;
    private final List<DistributionEntry> list;

    public WarehouseKeyListGroup(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.list = new ArrayList<>();
    }

    @Override
    public Warehouse getKey() {
        return warehouse;
    }

    @Override
    public List<DistributionEntry> getList() {
        return list;
    }
}
