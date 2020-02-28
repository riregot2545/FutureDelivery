package com.nix.futuredelivery;

import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.LocationConverter;
import com.nix.futuredelivery.repository.DistanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class FutureDeliveryApplicationTests {

	@Autowired
	private DistanceRepository repository;
	@Test
	void contextLoads() {
	}

	@Test
	void distanceTest() {
		Optional<Distance> oneByAddressFrom = repository.findOneByAddressFrom(
				new Address(1L, null, null, null,null ,
						null, null, null));
		System.out.println(oneByAddressFrom);
	}

}
