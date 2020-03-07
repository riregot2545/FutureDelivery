package com.nix.futuredelivery.entity;


import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.entity.value.Consumption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
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

    @Transient
    private Volume fullness;

    public Car(Long id, String model, Capacity capacity) {
        this.id = id;
        this.model = model;
        this.capacity = capacity;
        this.fullness = new Volume(0D);
    }

    public double getFreeVolume() {
        return capacity.getMaxVolume().getVolume() - fullness.getVolume();
    }

    public void resetFullness() {
        fullness.setVolume(0D);
    }

    public void fillVolume(Volume volume) {
        if (getFreeVolume() < volume.getVolume())
            throw new IllegalArgumentException("Filling volume is much than free volume");
        fullness.setVolume(fullness.getVolume() + volume.getVolume());
    }

}
