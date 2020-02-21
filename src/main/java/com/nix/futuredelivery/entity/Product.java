package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.ProductCategory;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_category")
    private final ProductCategory productCategory;
    @Column
    private final String name;
    @Column
    private final BigDecimal price;

    @ManyToMany(mappedBy = "warehouseProductCatalog")
    private final List<Warehouse> warehouses;
}
