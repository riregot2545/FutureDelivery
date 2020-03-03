package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractProductLine {
    @EmbeddedId
    private ProductLineId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("productId")
    private Product product;

    private int quantity;
}
