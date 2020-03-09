package com.nix.futuredelivery.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "store")
    private StoreManager storeManager;

    public Store(Long id, Address address, String name, StoreManager storeManager) {
        super(id, address, name);
        this.storeManager = storeManager;
    }

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreOrder> orders = new ArrayList<>();
}
