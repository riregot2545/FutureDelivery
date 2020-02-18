package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.value.LocationPoint;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Location {
    private final LocationPoint latitude;
    private final LocationPoint longitude;
}
