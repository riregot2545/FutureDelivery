package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String region;
    private final String country;
    private final String zipCode;

    private final Location pointLocation;
}
