package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import lombok.Data;

@Data
public class Address {
    private final String line1;
    private final String country;
    private final String city;
    private final Location location;
}
