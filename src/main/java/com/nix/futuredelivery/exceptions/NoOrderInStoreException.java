package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoOrderInStoreException extends ResponseStatusException {
    public NoOrderInStoreException(Long storeId, Long orderId) {
        super(HttpStatus.NOT_FOUND, "Store " + storeId + " has no order with id " + orderId);
    }
}
