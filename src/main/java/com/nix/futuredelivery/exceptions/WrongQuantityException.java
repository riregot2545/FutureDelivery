package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongQuantityException extends ResponseStatusException {
    public WrongQuantityException(Long productId, int quantity) {
        super(HttpStatus.CONFLICT, "Product "+productId+" cannot have quantity"+quantity);
    }
}