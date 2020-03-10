package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.StoreManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    StoreManager findByLogin(String login);
}
