package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.value.DomainName;
import lombok.Data;

@Data
public class EmailAddress {
    private final String login;
    private final DomainName domainName;
}
