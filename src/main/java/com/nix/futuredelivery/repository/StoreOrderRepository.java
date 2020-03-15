package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreOrderRepository extends JpaRepository<StoreOrder,Long> {
    List<StoreOrder> findByisDistributedFalse();
}
