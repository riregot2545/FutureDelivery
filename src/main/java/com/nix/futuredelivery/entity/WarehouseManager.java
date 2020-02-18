package com.nix.futuredelivery.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class WarehouseManager {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
