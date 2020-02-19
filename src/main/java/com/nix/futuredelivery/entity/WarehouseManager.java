package com.nix.futuredelivery.entity;

import javax.persistence.*;

public class WarehouseManager extends SystemUser{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "warehouse_manager_id")
    private Long id;

    @OneToOne(mappedBy = "warehouseManager")
    private Warehouse warehouse;
    public WarehouseManager(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }


}
