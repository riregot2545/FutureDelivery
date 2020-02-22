package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Store extends AbstractStation {
    @OneToOne
    private StoreManager storeManager;
}
