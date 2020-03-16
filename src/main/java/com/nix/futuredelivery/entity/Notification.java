package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public final class Notification {

    private String message;
    private boolean isEmpty;
    private List<Product> productList;
    private List<StoreManager> storeManagerList;
    private List<WarehouseManager> warehouseManagerList;

}
