package com.nix.futuredelivery.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class SystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated product ID")
    protected Long id;

    @NotNull(message = "First name is null.")
    protected String firstName;
    @NotNull(message = "Last name is null.")
    protected String lastName;

    @NotNull(message = "Login is null.")
    protected String login;
    @NotNull(message = "Password is null.")
    protected String password;

    @NotNull(message = "Email is null.")
    @Email
    protected String email;

    public SystemUser(Long id, String username, String password) {
        this.id = id;
        this.login = username;
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authorities
                = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }
}
