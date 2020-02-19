package com.nix.futuredelivery.entity.value;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Capacity {
    private double maxVolume;
}
