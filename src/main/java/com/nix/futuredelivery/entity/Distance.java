package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(DistanceId.class)
public class Distance implements Serializable {
    @ManyToOne
    @Id
    private Address addressFrom;

    @ManyToOne
    @Id
    private Address addressTo;

    @Positive(message = "Distance value is negative.")
    @NotNull(message = "Distance value is null.")
    private double distance;
}