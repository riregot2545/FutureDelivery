package com.nix.futuredelivery.repository;


import com.nix.futuredelivery.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseManagerRepository  extends JpaRepository<WarehouseManager, Long> {
    WarehouseManager findByLogin(String login);
    List<WarehouseManager> findByIsValidatedFalse();
}

