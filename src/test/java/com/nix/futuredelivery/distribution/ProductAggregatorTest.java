package com.nix.futuredelivery.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductAggregatorTest {
    @Autowired
    ProductAggregator productAggregator;
    @Test
    void aggregate() {
        productAggregator.aggregate();
    }
}