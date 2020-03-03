package com.nix.futuredelivery.entity.value;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProductLineId implements Serializable {
    private Long warehouse;
    private Long product;
}