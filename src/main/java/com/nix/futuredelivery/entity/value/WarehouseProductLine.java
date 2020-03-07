package com.nix.futuredelivery.entity.value;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.json.WarehouseProductLineDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@JsonDeserialize(using = WarehouseProductLineDeserializer.class)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(WarehouseProductLineId.class)
public class WarehouseProductLine extends AbstractProductLine implements Serializable {
    @JsonIgnore
    @ManyToOne
    @Id
    private Warehouse warehouse;

    public WarehouseProductLine(Product product, int quantity) {
        super(product, quantity);
    }
}