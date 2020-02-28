package com.nix.futuredelivery.entity.value;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductLineId implements Serializable {
    private Long storeOrder;
    private Long product;
}