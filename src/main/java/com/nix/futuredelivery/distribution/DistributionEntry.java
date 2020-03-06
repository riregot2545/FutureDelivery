package com.nix.futuredelivery.distribution;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DistributionEntry {
    private final Store store;
    private final Warehouse warehouse;
    private final Product product;
    private final List<OrderProductLine> orderProductLines;
}
