package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseManagerService {
    private WarehouseManagerRepository warehouseManagerRepository;
    private ProductRepository productRepository;
    private WarehouseRepository warehouseRepository;
    public WarehouseManagerService(WarehouseManagerRepository warehouseManagerRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository){
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }
    public WarehouseProductLinesOnly getProductLines(WarehouseManager manager){
        return warehouseRepository.findProductLinesByWarehouseManager(manager);
    }
    public void saveWarehouseManager(WarehouseManager manager){
        String password = manager.getPassword();
        manager.setPassword("{noop}"+password);
        warehouseManagerRepository.save(manager);
    }

    @Transactional
    public void saveProductLines(List<WarehouseProductLine> lines, String username){
        Optional<WarehouseManager> manager = Optional.ofNullable(warehouseManagerRepository.findByLogin(username));
        manager.ifPresent(man->warehouseRepository.findByWarehouseManager(man).setProductLines(lines));
    }

    public void saveWarehouse(Warehouse warehouse){
        warehouseRepository.save(warehouse);
    }
}
