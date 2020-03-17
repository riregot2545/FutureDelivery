package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.AbstractStation;
import com.nix.futuredelivery.entity.Distance;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Transportation model class that represent driven way with start and
 * destination with specified distance.
 */
@AllArgsConstructor
@Getter
public class RoadDriving {
    private AbstractStation startStation;
    private AbstractStation endStation;
    private Distance distance;
}
