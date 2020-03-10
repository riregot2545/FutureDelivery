package com.nix.futuredelivery.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class NoStationException extends ResponseStatusException {
    public NoStationException(Long id){
        super(HttpStatus.NOT_FOUND, "Manager " + id + " has no store or warehouse");
    }
}
