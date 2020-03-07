package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Store extends AbstractStation {
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "store")
    private StoreManager storeManager;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreOrder> orders = new ArrayList<>();
}
