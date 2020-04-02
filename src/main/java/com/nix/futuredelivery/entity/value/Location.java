package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @NotNull(message = "Geo location latitude is null.")
    @Min(value = -90, message = "Geo location latitude is out of bounds.")
    @Max(value = 90, message = "Geo location latitude is out of bounds.")
    private double latitude;
    @NotNull(message = "Geo location longitude is null.")
    @Min(value = -180, message = "Geo location longitude is out of bounds.")
    @Max(value = 180, message = "Geo location longitude is out of bounds.")
    private double longitude;
}
