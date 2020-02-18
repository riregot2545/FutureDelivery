package com.nix.futuredelivery.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final Location location;
    private final String name;
}
