package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministratorService {
    private RouteRepository routeRepository;
    private WarehouseRepository warehouseRepository;

    public AdministratorService(RouteRepository routeRepository, WarehouseRepository warehouseRepository) {
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
    }
    public List<Route> getActiveRoutes() {
        return routeRepository.findByClosedFalse();
    }

    public List<Warehouse> getWarehousesState() {
        return warehouseRepository.findAll();
    }


}
