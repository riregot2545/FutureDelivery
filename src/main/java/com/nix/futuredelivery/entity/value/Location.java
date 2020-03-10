package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private double latitude;
    private double longitude;
}
