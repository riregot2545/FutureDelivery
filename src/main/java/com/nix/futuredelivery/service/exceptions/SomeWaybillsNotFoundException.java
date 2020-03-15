package com.nix.futuredelivery.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

public class SomeWaybillsNotFoundException extends ResponseStatusException {
    public SomeWaybillsNotFoundException(List<Long> waybillIds) {
        super(HttpStatus.NOT_FOUND, "Some waybills not found, with ids = " +
                waybillIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }
}