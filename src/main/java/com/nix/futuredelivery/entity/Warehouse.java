package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Warehouse extends AbstractStation{
    @OneToOne(cascade = CascadeType.ALL)
    private WarehouseManager warehouseManager;

    @OneToMany(
            mappedBy = "warehouse",
            cascade = CascadeType.ALL)
    private List<WarehouseProductLine> productLines;

    public Warehouse(Long id, Address address, String name, WarehouseManager warehouseManager) {
        super(id, address, name);
        this.warehouseManager = warehouseManager;
    }
}

