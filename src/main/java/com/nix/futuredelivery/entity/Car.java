package com.nix.futuredelivery.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Consumption;
import com.nix.futuredelivery.entity.value.Volume;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Car model is empty.")
    @NotNull(message = "Car model is null.")
    private String model;

    @Valid
    @NotNull(message = "Car capacity is null.")
    @Embedded
    private Capacity capacity;

    @Transient
    private Volume fullness;

    @Valid
    @NotNull(message = "Car consumption is null.")
    @Embedded
    private Consumption consumption;

    public Car(Long id, String model, Capacity capacity, Consumption consumption) {
        this.id = id;
        this.model = model;
        this.capacity = capacity;
        this.consumption = consumption;
        this.fullness = new Volume(0D);
    }

    @JsonIgnore
    public double getFreeVolume() {
        return capacity.getMaxVolume().getVolumeWeight() - fullness.getVolumeWeight();
    }

    public void resetFullness() {
        fullness.setVolumeWeight(0D);
    }

    public void fillVolume(Volume volume) {
        if (getFreeVolume() < volume.getVolumeWeight())
            throw new IllegalArgumentException("Filling volume is much than free volume");
        fullness.setVolumeWeight(fullness.getVolumeWeight() + volume.getVolumeWeight());
    }

}
