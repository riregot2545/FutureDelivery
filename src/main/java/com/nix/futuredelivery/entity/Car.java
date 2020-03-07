package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Consumption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String model;

    @Embedded
    private Capacity capacity;

    @Embedded
    private Consumption consumption;
}
