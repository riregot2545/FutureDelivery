package com.nix.futuredelivery.distribution;

import com.nix.futuredelivery.transportation.DistributionGrouper;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistributionGrouperTest {
    @Autowired
    private DistributionGrouper distributionGrouper;


    @Test
    void distributeAllOrders() {
        List<DistributionEntry> distributionEntries = distributionGrouper.distributeAllOrders();
    }
}