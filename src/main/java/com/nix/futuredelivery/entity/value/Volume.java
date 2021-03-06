package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Volume {
    @Positive
    private double volumeWeight;

    public static Volume empty() {
        return new Volume(0D);
    }
}
