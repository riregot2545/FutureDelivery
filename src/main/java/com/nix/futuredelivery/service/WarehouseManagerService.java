package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseManagerService {
    private WarehouseManagerRepository warehouseManagerRepository;

    private WarehouseRepository warehouseRepository;
    private ProductService productService;

    public WarehouseManagerService(WarehouseManagerRepository warehouseManagerRepository, WarehouseRepository warehouseRepository, ProductService productService) {
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productService = productService;
    }

    @Transactional
    public List<WarehouseProductLine> getProductLines(Long id) {
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(() -> new IllegalStateException("no"));
        Warehouse warehouse = manager.getWarehouse();
        return warehouse.getProductLines();
    }

    @Transactional
    public void saveWarehouseManager(WarehouseManager manager) {
        String password = manager.getPassword();
        manager.setPassword("{noop}" + password);
        warehouseManagerRepository.save(manager);
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
    public void saveWarehouse(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
    }
}
