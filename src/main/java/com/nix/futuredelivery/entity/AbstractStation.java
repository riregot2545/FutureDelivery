package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public abstract class AbstractStation {


    private final Location location;
    private final String name;
}
