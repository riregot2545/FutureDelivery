package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.OrderLine;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Data
public class Waybill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final Set<OrderLine> orderLines;
}
