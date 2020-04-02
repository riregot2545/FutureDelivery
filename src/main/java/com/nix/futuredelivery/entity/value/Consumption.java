package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Consumption {
    @Positive(message = "Base consumption value is negative.")
    @NotNull(message = "Base consumption value is null.")
    private double baseConsumption;
    @Positive(message = "Relative consumption value is negative.")
    private double relativeConsumption;
}
