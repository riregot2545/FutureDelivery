package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Waybill;
import com.nix.futuredelivery.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorService {
    private RouteRepository routeRepository;

    public AdministratorService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> getActiveRoutes() {
        return routeRepository.findByClosedFalse();
    }

}
