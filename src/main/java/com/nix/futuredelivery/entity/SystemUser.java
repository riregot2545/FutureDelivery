package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.EmailAddress;
import com.nix.futuredelivery.entity.value.EmailConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class SystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected String firstName;
    protected String lastName;

    protected String login;
    protected String password;


    protected String email;

    public SystemUser(Long id, String username, String password) {
        this.id = id;
        this.login = username;
        this.password = password;
    }
}
