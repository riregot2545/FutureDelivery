package com.nix.futuredelivery;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.LocationConverter;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.StoreRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class FutureDeliveryApplicationTests {

	@Autowired
	private DistanceRepository repository;
	private StoreRepository storeRepository;
	private WarehouseRepository warehouseRepository;
	private ProductRepository productRepository;
	@Test
	void contextLoads() {
	}
}
