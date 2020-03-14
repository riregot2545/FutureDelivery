package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Driver;
import com.nix.futuredelivery.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByDriverAndIsClosedFalse(Driver driver);
}
