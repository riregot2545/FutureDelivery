package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoPersonException extends ResponseStatusException {
    public NoPersonException(String role, Long id){
        super(HttpStatus.NOT_FOUND, role+" " + id + " does not exist");
    }
}
