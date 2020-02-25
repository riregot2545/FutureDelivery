package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<StoreOrder,Long> {


}
