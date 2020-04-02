package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nix.futuredelivery.entity.value.Volume;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @ColumnDefault("false")
    private boolean isConfirmed;

    @Valid
    @ManyToOne
    private ProductCategory productCategory;

    @NotEmpty(message = "Product name is empty.")
    @NotNull(message = "Product name is null.")
    private String name;

    @Positive(message = "Price negative or zero.")
    @NotNull(message = "Product price is null.")
    private BigDecimal price;

    @Valid
    @Embedded
    @NotNull(message = "Product volume is null.")
    private Volume volume;

    public Product(String name) {
        this.name = name;
    }
}