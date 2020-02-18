package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
}
