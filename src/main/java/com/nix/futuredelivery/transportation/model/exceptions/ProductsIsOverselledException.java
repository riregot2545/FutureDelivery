package com.nix.futuredelivery.transportation.model.exceptions;

public class ProductsIsOverselledException extends Exception {
    public ProductsIsOverselledException(int realQuantity, int orderedQuantity) {
        super("Product quantity in orders is greater than warehouse stock: real - "
                + realQuantity + " ordered - " + orderedQuantity);
    }
}
