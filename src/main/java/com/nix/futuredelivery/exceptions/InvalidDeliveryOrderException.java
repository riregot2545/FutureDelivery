package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidDeliveryOrderException extends ResponseStatusException {
    public InvalidDeliveryOrderException(int expectedOrder, int realOrder) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Unexpected delivery order: " + realOrder + " where expected " + expectedOrder);
    }
}