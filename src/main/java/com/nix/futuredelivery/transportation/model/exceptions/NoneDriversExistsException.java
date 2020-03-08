package com.nix.futuredelivery.transportation.model.exceptions;

public class NoneDriversExistsException extends Exception {
    public NoneDriversExistsException() {
        super("None drivers exists in database");
    }
}
