package com.nix.futuredelivery.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Driver extends SystemUser{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public Driver(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }
}
