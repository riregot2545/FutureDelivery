package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Warehouse extends AbstractStation{
    @OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private WarehouseManager warehouseManager;

    @OneToMany(
            mappedBy = "warehouse",
            cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WarehouseProductLine> productLines = new ArrayList<>();

    public Warehouse(Long id, Address address, String name, WarehouseManager warehouseManager) {
        super(id, address, name);
        this.warehouseManager = warehouseManager;
    }
}

