package com.nix.futuredelivery.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

import java.util.Set;


public class Store extends AbstractStation{
    private final StoreManager storeManager;
    public Store(Location location, String name, StoreManager storeManager) {
        super(location, name);
        this.storeManager = storeManager;
    }
}
