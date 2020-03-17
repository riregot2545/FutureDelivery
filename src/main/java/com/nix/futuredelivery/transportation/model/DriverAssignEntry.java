package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Transportation model class that used for represent and support
 * fairly driver load.
 */
@AllArgsConstructor
@Getter
public class DriverAssignEntry {
    private Driver driver;
    private Long assignCount;

    public void incrementAssign() {
        assignCount++;
    }
}
