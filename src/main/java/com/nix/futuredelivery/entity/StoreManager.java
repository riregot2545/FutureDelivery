package com.nix.futuredelivery.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StoreManager extends SystemUser{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public StoreManager(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }
}
