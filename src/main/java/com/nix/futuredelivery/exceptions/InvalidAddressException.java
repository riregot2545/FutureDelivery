package com.nix.futuredelivery.exceptions;

import com.nix.futuredelivery.entity.Address;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(Address address) {
        super(String.format("Address %s is invalid.", address));
    }
}
