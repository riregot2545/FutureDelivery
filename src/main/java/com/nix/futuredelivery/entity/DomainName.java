package com.nix.futuredelivery.entity;

import lombok.Data;

import java.util.List;

@Data
public class DomainName {
    private final List<String> labels;
}
