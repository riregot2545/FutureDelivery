package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nix.futuredelivery.entity.value.Volume;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated product ID")
    private Long id;

    @ManyToOne
    private ProductCategory productCategory;
    @Column
    private String name;
    @Column
    private BigDecimal price;

    @Embedded
    private Volume volume;

    public Product(String name) {
        this.name = name;
    }
}
