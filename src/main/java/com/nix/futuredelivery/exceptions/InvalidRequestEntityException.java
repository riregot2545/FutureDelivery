package com.nix.futuredelivery.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidRequestEntityException extends ResponseStatusException {
    public InvalidRequestEntityException(List<ObjectError> errors) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid request entity:%s", errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"))));
    }
}