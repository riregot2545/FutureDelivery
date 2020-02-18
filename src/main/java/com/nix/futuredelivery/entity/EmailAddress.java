package com.nix.futuredelivery.entity;

import lombok.Data;

@Data
public class EmailAddress {
    private final String login;
    private final DomainName domainName;
}
