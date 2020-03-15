package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.exceptions.WrongQuantityException;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractProductLine implements Serializable {
    @ManyToOne(cascade = CascadeType.MERGE)
    @Id
    protected Product product;

    protected int quantity;

    public AbstractProductLine(Product product, int quantity) {
        this.product = product;
        if(quantity<0) throw new WrongQuantityException(product.getId(), quantity);
        this.quantity = quantity;
    }

    public void setQuantity(int quantity){
        if (quantity < 0) throw new WrongQuantityException(product.getId(), quantity);
        this.quantity = quantity;
    }
}
