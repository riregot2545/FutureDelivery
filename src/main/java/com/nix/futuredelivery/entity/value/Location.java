package com.nix.futuredelivery.entity.value;

import lombok.Data;

import javax.persistence.Entity;

@Data
public class Location {
    private final double latitude;
    private final double longitude;
}
