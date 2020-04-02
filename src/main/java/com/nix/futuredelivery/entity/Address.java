package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.LocationConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated product ID")
    private Long id;

    @NotNull(message = "Address line 1 is null.")
    private String addressLine1;
    private String addressLine2;
    @NotNull(message = "City is null.")
    private String city;
    @NotNull(message = "Region is null.")
    private String region;
    @NotNull(message = "Country is null.")
    private String country;
    @NotNull(message = "Zip is null.")
    private String zipCode;

    @Convert(converter = LocationConverter.class)
    @NotNull(message = "Geo point location is null.")
    private Location pointLocation;
}
