package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    SystemUser findByLogin(String login);
}
