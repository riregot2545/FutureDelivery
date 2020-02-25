package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class AbstractProductLine {
    @EmbeddedId
    private ProductLineId id;

    @ManyToOne
    @MapsId("productId")
    private Product product;

    private int quantity;
}
