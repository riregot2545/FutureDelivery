package com.nix.futuredelivery.entity;

import lombok.Data;

import java.util.List;

@Data
public class Route {
    private final Driver driver;
    private final Car car;

    private final List<Waybill> waybillList;
    private final Warehouse warehouse;
    private final List<Store> stations;
}
