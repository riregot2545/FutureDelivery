package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Store extends AbstractStation{
    @Column(name = "store_manager")
    private final StoreManager storeManager;
    public Store(Location location, String name, StoreManager storeManager) {
        super(location, name);
        this.storeManager = storeManager;
    }
}
