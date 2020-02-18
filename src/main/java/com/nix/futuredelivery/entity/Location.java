package com.nix.futuredelivery.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Location {
    private final LocationPoint latitude;
    private final LocationPoint longitude;
}
