package com.nix.futuredelivery.exceptions;

import com.nix.futuredelivery.entity.Address;

public class DistanceNotFoundException extends Exception {
    public DistanceNotFoundException(Address from, Address to) {
        super(String.format("Can't found distance between 2 address %s and %s.", from, to));
    }
}
