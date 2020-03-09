package com.nix.futuredelivery.transportation.tsolver.model;

import com.nix.futuredelivery.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Consumer {
    private int demand;
    private final Store store;
    private final boolean isFictive;
}
