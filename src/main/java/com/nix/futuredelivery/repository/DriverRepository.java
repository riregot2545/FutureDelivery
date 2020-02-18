package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Driver;
import org.springframework.data.repository.CrudRepository;

public interface DriverRepository extends CrudRepository<Driver,Long> {
}
