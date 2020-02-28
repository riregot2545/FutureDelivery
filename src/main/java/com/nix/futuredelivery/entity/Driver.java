package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver extends SystemUser{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Driver(Long id, String firstName, String lastName, String login, String password, String email) {
        super(id, firstName, lastName, login, password, email);
    }
}
