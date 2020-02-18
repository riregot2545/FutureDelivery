package com.nix.futuredelivery.entity;

import lombok.Data;

@Data
public class Location {
    private final LocationPoint latitude;
    private final LocationPoint longitude;

    private final Address address;
}
