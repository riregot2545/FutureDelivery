package com.nix.futuredelivery.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoreManager extends SystemUser{
    @OneToOne
    private Store store;
}
