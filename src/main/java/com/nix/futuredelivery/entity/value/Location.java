package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Data
@AllArgsConstructor
public class Location {
    private final double latitude;
    private final double longitude;
}
