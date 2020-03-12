package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoProductException extends ResponseStatusException {
    public NoProductException(Long id) {
        super(HttpStatus.NOT_FOUND, "Product " + id + " does not exist");
    }
}
