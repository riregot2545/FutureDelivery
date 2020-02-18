package com.nix.futuredelivery.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class StoreManager {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private final String firstName;
    private final String lastName;
    private final String login;
    private final String password;
}
