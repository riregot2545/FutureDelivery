package com.nix.futuredelivery.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoStationException extends IllegalArgumentException{
    @Override
    public String getMessage() {
        return "Manager does not have store or warehouse";
    }
}
