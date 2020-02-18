package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Waybill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaybillRepository extends JpaRepository<Waybill, Long> {
}
