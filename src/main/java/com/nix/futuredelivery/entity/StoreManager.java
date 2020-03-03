package com.nix.futuredelivery.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoreManager extends SystemUser{
    @OneToOne
    private Store store;

    public StoreManager(Long id, String firstName, String lastName, String login, String password, String email, Store store) {
        super(id, firstName, lastName, login, password, email);
        this.store = store;
    }
}
