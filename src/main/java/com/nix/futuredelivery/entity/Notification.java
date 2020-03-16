package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Notification {

    private String message;
    private boolean isEmpty;

}
