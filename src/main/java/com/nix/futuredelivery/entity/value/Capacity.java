package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Capacity {
    @Embedded
    private Volume maxVolume;
}
