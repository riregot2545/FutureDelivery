package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Warehouse extends AbstractStation{
    @OneToOne
    private WarehouseManager warehouseManager;

    @OneToMany(
            mappedBy = "warehouse",
            cascade = CascadeType.ALL)
    private List<WarehouseProductLine> productLines;
}

