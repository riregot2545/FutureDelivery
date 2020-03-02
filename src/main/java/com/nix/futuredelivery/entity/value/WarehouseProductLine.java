package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WarehouseProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @MapsId("documentId")
    private Warehouse warehouse;
}