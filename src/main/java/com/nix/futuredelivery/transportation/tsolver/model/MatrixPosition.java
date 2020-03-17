package com.nix.futuredelivery.transportation.tsolver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Transportation solver model class used for matrix point structure
 */
@Data
@AllArgsConstructor
public class MatrixPosition {
    private final int x;
    private final int y;
}
