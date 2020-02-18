package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.Data;

@Data
public class OrderLine {
    private final Product product;
    private final int quantity;
}
