package com.nix.futuredelivery.entity.value;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductLineId implements Serializable {
    private Long documentId;
    private Long productId;
}