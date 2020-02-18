package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
}
