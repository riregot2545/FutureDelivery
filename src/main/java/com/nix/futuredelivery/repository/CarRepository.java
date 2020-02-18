package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<Car,Long> {
}
