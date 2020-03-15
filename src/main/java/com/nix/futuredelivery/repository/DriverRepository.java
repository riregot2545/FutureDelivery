package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Driver;
import com.nix.futuredelivery.transportation.model.DriverLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    @Query("SELECT " +
            "    new com.nix.futuredelivery.transportation.model.DriverLoad(d, COUNT(d)) " +
            "FROM Route r RIGHT JOIN r.driver d " +
            "GROUP BY d.id"
    )
    List<DriverLoad> aggregateDriverByLoad();

    Driver findByLogin(String login);
}
