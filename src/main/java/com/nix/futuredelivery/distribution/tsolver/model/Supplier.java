package com.nix.futuredelivery.distribution.tsolver.model;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Supplier {
    private int supply;
    private final Warehouse warehouse;
}
