package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class DistributionEntry {
    private final DistributionKey distributionKey;
    private final List<OrderProductLine> orderProductLines;

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class DistributionKey {
        private final Store store;
        private final Product product;
        private final Warehouse warehouse;
    }
}
