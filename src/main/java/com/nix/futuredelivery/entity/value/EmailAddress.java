package com.nix.futuredelivery.entity.value;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Data
@AllArgsConstructor
@Embeddable
public class EmailAddress {
    private  String login;
    @Embedded
    private  EmailDomainName emailDomainName;
}
