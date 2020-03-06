package com.nix.futuredelivery.transportation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransportationProcessorTest {
    @Autowired
    private TransportationProcessor transportationProcessor;

    @Test
    void proceedOrders() {
        transportationProcessor.proceedOrders();
    }
}