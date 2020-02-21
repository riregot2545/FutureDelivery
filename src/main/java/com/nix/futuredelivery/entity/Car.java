package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Capacity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private final Driver driver;

    @Column
    private final Capacity capacity;
}
