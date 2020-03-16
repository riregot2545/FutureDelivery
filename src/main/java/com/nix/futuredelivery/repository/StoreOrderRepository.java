package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreOrderRepository extends JpaRepository<StoreOrder,Long> {
    List<StoreOrder> findByOrderStatus(OrderStatus status);
}
