package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Waybill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaybillRepository extends JpaRepository<Waybill, Long> {
    List<Waybill> findByStoreOrder(StoreOrder order);
}
