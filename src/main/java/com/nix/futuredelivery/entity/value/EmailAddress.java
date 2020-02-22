package com.nix.futuredelivery.entity.value;

import lombok.Data;

@Data
public class EmailAddress {
    private final String login;
    private final EmailDomainName emailDomainName;


}
