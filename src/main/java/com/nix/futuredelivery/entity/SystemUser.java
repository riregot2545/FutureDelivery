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
    private Long id;

    private String firstName;
    private String lastName;

    private String login;
    private String password;

    @Convert(converter = EmailConverter.class)
    private EmailAddress email;
}
