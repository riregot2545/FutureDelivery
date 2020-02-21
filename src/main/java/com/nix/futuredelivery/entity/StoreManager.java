package com.nix.futuredelivery.entity;


import javax.persistence.Entity;

@Entity
public class StoreManager extends SystemUser{

    public StoreManager(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }
}
