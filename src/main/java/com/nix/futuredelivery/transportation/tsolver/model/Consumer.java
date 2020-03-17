package com.nix.futuredelivery.transportation.tsolver.model;

import com.nix.futuredelivery.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Transportation solver model that used as consumer transformation (stores for example). It is connected with transformation by {@code store} field.
 * May be fictive, that means it was created to consume extra supply.
 */
@Data
@AllArgsConstructor
public class Consumer {
    private int demand;
    private final Store store;
    private final boolean isFictive;
}
