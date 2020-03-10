package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaybillProductLineId implements Serializable {
    private Long waybill;
    private Long product;
}
