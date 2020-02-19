package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    List<StoreManager> findAll();
    Optional<StoreManager> findById(Long id);
    Long save(StoreManager storeManager);
}
