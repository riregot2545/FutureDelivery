package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;

@Data
@MappedSuperclass
public class AbstractProductLine {
    @EmbeddedId
    private ProductLineId id;

    @ManyToOne
    @MapsId("productId")
    private Product product;

    private int quantity;
}
