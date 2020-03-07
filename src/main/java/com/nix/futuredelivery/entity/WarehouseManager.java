package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WarehouseManager extends SystemUser{
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authorities
                = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("WAREHOUSE_MANAGER"));
        return authorities;
    }
}
