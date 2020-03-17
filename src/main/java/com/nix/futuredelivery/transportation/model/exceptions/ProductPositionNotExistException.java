package com.nix.futuredelivery.transportation.model.exceptions;

public class ProductPositionNotExistException extends Exception {
    public ProductPositionNotExistException() {
        super("Product positions in warehouse stock and orders does not compliance");
    }
}
