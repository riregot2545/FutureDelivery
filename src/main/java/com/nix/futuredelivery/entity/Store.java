package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;

import javax.persistence.*;

@Entity
public class Store extends AbstractStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "store_manager")
    private final StoreManager storeManager;

    public Store(Location location, String name, StoreManager storeManager) {
        super(location, name);
        this.storeManager = storeManager;
    }
}
