package com.nix.futuredelivery.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_category")
    private final ProductCategory productCategory;
    @Column
    private final String name;
    @Column
    private final BigDecimal price;
}
