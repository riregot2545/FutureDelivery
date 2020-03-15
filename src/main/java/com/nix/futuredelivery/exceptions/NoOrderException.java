package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoOrderException extends ResponseStatusException {
    public NoOrderException(Long id){
        super(HttpStatus.NOT_FOUND, "Order " + id + " does not exist");
    }
}
