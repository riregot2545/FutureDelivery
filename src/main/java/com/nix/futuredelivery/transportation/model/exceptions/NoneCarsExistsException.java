package com.nix.futuredelivery.transportation.model.exceptions;

public class NoneCarsExistsException extends Exception {
    public NoneCarsExistsException() {
        super("None cars exists in database");
    }
}
