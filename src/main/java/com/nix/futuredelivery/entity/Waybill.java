package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.CheckedOrderLine;
import com.nix.futuredelivery.entity.value.OrderLine;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
public class Waybill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final Store store;
    private final List<CheckedOrderLine> orderLines;
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private final Route route;

}
