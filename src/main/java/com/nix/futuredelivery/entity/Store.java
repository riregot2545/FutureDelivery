package com.nix.futuredelivery.entity;

import lombok.Data;

@Data
public class Store {
    private final Location location;
    private final StoreManager storeManager;
    private final String name;
}
