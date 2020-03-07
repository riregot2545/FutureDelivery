package com.nix.futuredelivery.repository;


import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
     WarehouseProductLinesOnly findProductLinesByWarehouseManager(WarehouseManager manager);
     Optional<Warehouse> findByWarehouseManager(WarehouseManager manager);
}
