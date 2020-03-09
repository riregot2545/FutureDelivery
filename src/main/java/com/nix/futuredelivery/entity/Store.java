package com.nix.futuredelivery.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Store extends AbstractStation {
    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "store")
    private StoreManager storeManager;

    public Store(Long id, Address address, String name, StoreManager storeManager) {
        super(id, address, name);
        this.storeManager = storeManager;
    }

    public Store(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreOrder> orders = new ArrayList<>();

    public void addOrder(StoreOrder order){
        orders.add(order);
    }
}
