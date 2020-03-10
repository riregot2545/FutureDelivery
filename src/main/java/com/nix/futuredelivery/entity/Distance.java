package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    private double distance;
}