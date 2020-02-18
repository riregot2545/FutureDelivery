package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Warehouse extends AbstractStation{

    private final List<Product> warehouseProductCatalog;
    private final WarehouseManager warehouseManager;

    public Warehouse(Location location, String name, List<Product> warehouseProductCatalog, WarehouseManager warehouseManager) {
        super(location, name);
        this.warehouseProductCatalog = warehouseProductCatalog;
        this.warehouseManager = warehouseManager;
    }
}
