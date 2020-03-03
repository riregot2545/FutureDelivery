package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Volume;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ProductCategory productCategory;
    @Column
    private String name;
    @Column
    private BigDecimal price;

    @Embedded
    private Volume volume;

}
