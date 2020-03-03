package com.nix.futuredelivery.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoreManager extends SystemUser{
    @OneToOne
    private Store store;

    public StoreManager(Long id, String firstName, String lastName, String login, String password, String email, Store store) {
        super(id, firstName, lastName, login, password, email);
        this.store = store;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authorities
                = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("STORE_MANAGER"));
        return authorities;
    }
}
