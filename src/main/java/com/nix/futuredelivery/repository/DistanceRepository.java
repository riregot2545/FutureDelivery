package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Distance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistanceRepository extends JpaRepository<Distance,Long> {
    Optional<Distance> findOneByAddressFromAndAddressTo(Address addressFrom,Address addressTo);

    List<Distance> findByAddressFrom(Address addressFrom);
}
