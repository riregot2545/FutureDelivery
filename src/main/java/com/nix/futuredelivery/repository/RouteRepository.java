package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByClosedFalse();
}
