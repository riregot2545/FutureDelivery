package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StoreManager extends SystemUser{

    public StoreManager(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }
}
