package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.repository.*;
import com.nix.futuredelivery.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdministratorService {
    private RouteRepository routeRepository;
    private WarehouseRepository warehouseRepository;
    private WarehouseManagerRepository warehouseManagerRepository;
    private StoreManagerRepository storeManagerRepository;
    private ProductRepository productRepository;


    public AdministratorService(RouteRepository routeRepository, WarehouseRepository warehouseRepository, WarehouseManagerRepository warehouseManagerRepository, StoreManagerRepository storeManagerRepository, ProductRepository productRepository) {
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
        this.storeManagerRepository = storeManagerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.productRepository = productRepository;
    }

    public List<Route> getActiveRoutes() {
        return routeRepository.findByClosedFalse();
    }

    public List<Warehouse> getWarehousesState() {
        return warehouseRepository.findAll();
    }

    public Notification getNotification() {
        List<Product> products = productRepository.findByIsValidatedFalse();
        List<WarehouseManager> warehouseManagers = warehouseManagerRepository.findByIsValidatedFalse();
        List<StoreManager> storeManagers = storeManagerRepository.findByIsValidatedFalse();
        Notification notification;
        if (products.isEmpty() && warehouseManagers.isEmpty() &&
                storeManagers.isEmpty()) {
            notification = new Notification("Nothing to validate!", true, null, null, null);
        } else {
            notification = new Notification("Have an unvalidated users or products!", false, products, storeManagers, warehouseManagers);

        }
        return notification;
    }

    @Transactional
    public void confirmProduct(List<Product> productList) {
        for (int i = 0; i != productList.size(); i++) {
          Product  p = productRepository.findById(productList.get(i).getId()).orElseThrow(() -> new IllegalArgumentException("no"));
            p.setValidated(true);
            productRepository.save(p);
        }

    }
}

