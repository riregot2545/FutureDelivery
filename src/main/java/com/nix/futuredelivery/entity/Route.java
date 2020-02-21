package com.nix.futuredelivery.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "route_id")
    private Long id;
    private final Driver driver;
    private final Car car;

    @OneToMany(mappedBy = "route")
    private final List<Waybill> waybillList;
    private final Warehouse warehouse;
    private final List<Store> stations;
}
