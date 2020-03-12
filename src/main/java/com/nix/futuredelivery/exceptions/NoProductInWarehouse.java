package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoProductInWarehouse extends ResponseStatusException {
    public NoProductInWarehouse(Long productId, Long warehouseId) {
        super(HttpStatus.NOT_FOUND, "Warehouse " + warehouseId + " doesn't have product " + productId);
    }
}