package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Capacity {
    @Embedded
    @Valid
    private Volume maxVolume;
}
