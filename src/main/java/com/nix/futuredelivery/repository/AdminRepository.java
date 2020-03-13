package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository  extends JpaRepository<Admin, Long> {
    Admin findByLogin(String login);
}
