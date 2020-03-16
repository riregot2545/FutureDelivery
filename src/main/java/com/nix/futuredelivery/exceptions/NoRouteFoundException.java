package com.nix.futuredelivery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoRouteFoundException extends ResponseStatusException {
    public NoRouteFoundException(Long routeId) {
        super(HttpStatus.NOT_FOUND, "No route found with id=" + routeId);
    }
}