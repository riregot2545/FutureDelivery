package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Distance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistanceRepository extends JpaRepository<Distance,Long> {
    Optional<Distance> findOneByAddressFrom(Address addressFrom);
}
