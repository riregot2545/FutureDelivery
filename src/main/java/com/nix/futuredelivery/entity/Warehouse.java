package com.nix.futuredelivery.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Warehouse extends AbstractStation{
    private final Set<Product> warehouseProductCategory;
    private final WarehouseManager warehouseManager;
    public Warehouse(Location location, String name, Set<Product> warehouseProductCategory, WarehouseManager warehouseManager) {
        super(location, name);
        this.warehouseProductCategory = warehouseProductCategory;
        this.warehouseManager = warehouseManager;
    }
}
