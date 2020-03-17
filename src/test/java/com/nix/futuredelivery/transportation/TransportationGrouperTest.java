package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOversellsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransportationGrouperTest {
    @Autowired
    private TransportationGrouper transportationGrouper;


    @Test
    void distributeAllOrders() throws ProductsIsOversellsException {
        List<DistributionEntry> distributionEntries = transportationGrouper.distributeAllNewOrders();
    }
}