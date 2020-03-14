package com.nix.futuredelivery.entity;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Store extends AbstractStation {
    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private StoreManager storeManager;

    public Store(Long id, Address address, String name, StoreManager storeManager) {
        super(id, address, name);
        this.storeManager = storeManager;
    }
}
