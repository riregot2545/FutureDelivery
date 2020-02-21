package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.Getter;
import lombok.Setter;


public class CheckedOrderLine extends OrderLine {
    @Getter
    private final boolean isDelivered;

    public CheckedOrderLine(Product product, int quantity) {
        super(product, quantity);
        this.isDelivered = false;
    }
}
