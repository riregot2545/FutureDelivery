package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.LocationConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated product ID")
    private Long id;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String country;
    private String zipCode;

    @Convert(converter = LocationConverter.class)
    private Location pointLocation;
}
