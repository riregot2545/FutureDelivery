package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WarehouseManager extends SystemUser{
    @OneToOne
    private Warehouse warehouse;

    public WarehouseManager(Long id, String firstName, String lastName, String login, String password, String email, Warehouse warehouse) {
        super(id, firstName, lastName, login, password, email);
        this.warehouse = warehouse;
    }
}
