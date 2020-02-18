package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.OrderLine;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Set<OrderLine> orderLines;
}
