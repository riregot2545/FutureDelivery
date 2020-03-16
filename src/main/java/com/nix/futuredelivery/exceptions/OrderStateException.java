package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OrderStateException extends ResponseStatusException {
    public OrderStateException(Long id) {
        super(HttpStatus.CONFLICT, "Order "+id+ " can't be closed.");
    }
}
