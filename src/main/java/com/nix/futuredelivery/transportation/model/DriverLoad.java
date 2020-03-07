package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DriverLoad {
    private Driver driver;
    private Long load;

    public void incrementLoad() {
        load++;
    }
}
