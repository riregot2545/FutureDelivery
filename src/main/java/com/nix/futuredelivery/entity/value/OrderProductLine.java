package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.StoreOrder;
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
@IdClass(OrderProductLineId.class)
public class OrderProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @Id
    private StoreOrder storeOrder;

    public OrderProductLine(Product product, int quantity, StoreOrder storeOrder) {
        super(product, quantity);
        this.storeOrder = storeOrder;
    }
}
