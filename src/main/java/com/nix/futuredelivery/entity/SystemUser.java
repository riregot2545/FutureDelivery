package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
public abstract class SystemUser {

    @Column
    private final String firstName;
    @Column
    private final String lastName;
    @Column
    private final String login;
    @Column
    private final String password;
}
