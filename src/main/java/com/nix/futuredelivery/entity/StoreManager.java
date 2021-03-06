package com.nix.futuredelivery.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoreManager extends SystemUser{

    @ColumnDefault("false")
    private boolean isConfirmed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    @ApiModelProperty(notes = "The store that belongs to this manager")
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
