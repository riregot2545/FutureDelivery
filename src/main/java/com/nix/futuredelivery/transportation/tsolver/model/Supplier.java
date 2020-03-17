package com.nix.futuredelivery.transportation.tsolver.model;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model that used as supplier transformation (warehouse for example). It is connected with transformation by {@code warehouse} field.
 */
@Data
@AllArgsConstructor
public class Supplier {
    private int supply;
    private final Warehouse warehouse;
}
