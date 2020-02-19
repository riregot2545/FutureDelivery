package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class Warehouse extends AbstractStation{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "warehouse_id")
    private Long id;

    @Column(name = "warehouse_manager")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouse")
    private final WarehouseManager warehouseManager;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "warehouse_products",
            joinColumns = {@JoinColumn(name = "warehouse_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_id")})
    private final List<Product> warehouseProductCatalog;

    public Warehouse(Location location, String name, List<Product> warehouseProductCatalog, WarehouseManager warehouseManager) {
        super(location, name);
        this.warehouseProductCatalog = warehouseProductCatalog;
        this.warehouseManager = warehouseManager;
    }
}
