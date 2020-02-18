package com.nix.futuredelivery.entity;

import lombok.Data;

@Data
public class Address {
    private final String line1;
    private final String country;
    private final String city;
}
