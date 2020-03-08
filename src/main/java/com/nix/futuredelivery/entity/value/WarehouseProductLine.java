package com.nix.futuredelivery.entity.value;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(WarehouseProductLineId.class)
public class WarehouseProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @Id
    @JsonBackReference
    private Warehouse warehouse;
}