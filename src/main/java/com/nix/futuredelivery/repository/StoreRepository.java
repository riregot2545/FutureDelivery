package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
