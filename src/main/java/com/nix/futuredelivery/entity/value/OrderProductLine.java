package com.nix.futuredelivery.entity.value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.json.WarehouseProductLineDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@JsonDeserialize(using = WarehouseProductLineDeserializer.class)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(OrderProductLineId.class)
public class OrderProductLine extends AbstractProductLine implements Serializable {
    @JsonIgnore
    @ManyToOne
    @Id
    private StoreOrder storeOrder;

    public OrderProductLine(Product product, int quantity, StoreOrder storeOrder) {
        super(product, quantity);
        this.storeOrder = storeOrder;
    }
}
