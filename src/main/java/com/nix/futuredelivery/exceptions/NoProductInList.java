package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoProductInList extends ResponseStatusException {
public NoProductInList(Long productId, Long listId, String list) {
        super(HttpStatus.NOT_FOUND, list+" " + listId + " doesn't have product " + productId);
        }
        }