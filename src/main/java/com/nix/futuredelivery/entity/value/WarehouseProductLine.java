package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(WarehouseProductLineId.class)
public class WarehouseProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @Id
    private Warehouse warehouse;
}