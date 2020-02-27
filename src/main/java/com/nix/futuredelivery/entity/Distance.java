package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.ProductLineId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Distance {
    @EmbeddedId
    private DistanceId id;

    @ManyToOne
    @MapsId("fromId")
    private Address addressFrom;

    @ManyToOne
    @MapsId("toId")
    private Address addressTo;

    private double distance;
}
