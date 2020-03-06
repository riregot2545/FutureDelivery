package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

