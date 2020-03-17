package com.nix.futuredelivery.transportation.model.exceptions;

public class ProductsIsOversellsException extends Exception {
    public ProductsIsOversellsException(int realQuantity, int orderedQuantity) {
        super("Product quantity in orders is greater than warehouse stock: real - "
                + realQuantity + " ordered - " + orderedQuantity);
    }
}
