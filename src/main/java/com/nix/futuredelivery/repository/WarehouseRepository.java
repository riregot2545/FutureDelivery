package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
