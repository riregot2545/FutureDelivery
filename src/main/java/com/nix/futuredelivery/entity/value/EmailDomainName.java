package com.nix.futuredelivery.entity.value;

import lombok.Data;

import java.util.List;

@Data
public class EmailDomainName {
    private final List<String> domains;
}
