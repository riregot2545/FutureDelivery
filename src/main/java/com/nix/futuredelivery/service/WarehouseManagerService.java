package com.nix.futuredelivery.service;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoPersonException;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class WarehouseManagerService {
    private WarehouseManagerRepository warehouseManagerRepository;
    private WarehouseRepository warehouseRepository;
    private ProductService productService;
    private PasswordEncoder passwordEncoder;
    private DistanceService distanceService;

    public WarehouseManagerService(WarehouseManagerRepository warehouseManagerRepository, WarehouseRepository warehouseRepository, ProductService productService, PasswordEncoder passwordEncoder, DistanceService distanceService) {
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productService = productService;
        this.passwordEncoder = passwordEncoder;
        this.distanceService = distanceService;
    }

    @Transactional
    public Long saveWarehouseManager(WarehouseManager manager) {
        String password = manager.getPassword();
        manager.setPassword(passwordEncoder.encode(password));
        WarehouseManager warehouseManager = warehouseManagerRepository.saveAndFlush(manager);
        return warehouseManager.getId();
    }
    @Transactional
    public List<WarehouseProductLine> getProductLines(Long id) {
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Warehouse manager", id));
        Warehouse warehouse = manager.getWarehouse();
        List<WarehouseProductLine> lines = warehouse.getProductLines();
        lines.size();
        return lines;
    }



    @Transactional
    public boolean hasWarehouse(Long id) {
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Manager with id " + id + " does not exist"));
        return manager.getWarehouse() != null;
    }


    @Transactional
    public void saveProductLines(List<Product> lines, Long id) {
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Manager with id " + id + " does not exist"));
        Warehouse warehouse = manager.getWarehouse();
        productService.addProductsToWarehouse(lines, warehouse);
    }

    @Transactional
    public void editProductLines(List<WarehouseProductLine> lines, Long id) {
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Manager with id " + id + " does not exist"));
        Warehouse warehouse = manager.getWarehouse();
        productService.editProductsOfWarehouse(lines, warehouse);
    }

    @Transactional
    public void saveWarehouse(Warehouse warehouse) throws InterruptedException, ApiException, IOException {
        warehouseRepository.save(warehouse);
        distanceService.addNewPoint(warehouse.getAddress());
    }
}
