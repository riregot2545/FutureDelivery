package com.nix.futuredelivery.service;

import com.nix.futuredelivery.repository.StoreOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreManagerServiceTest {
    @Autowired
    private StoreOrderRepository storeOrderRepository;
    @Test
    void deleteOrder() {
        storeOrderRepository.deleteById((long) 39);
        assertNull(storeOrderRepository.findAll());
    }
}