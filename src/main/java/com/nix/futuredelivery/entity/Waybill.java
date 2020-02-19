package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.WaybillProductLine;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
public class Waybill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final Store store;

    @OneToMany(
            mappedBy = "waybill",
            cascade = CascadeType.ALL
    )
    private final List<WaybillProductLine> productLines;

    // TODO: 21.02.2020 Map waybill and route
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private final Route route;

}
