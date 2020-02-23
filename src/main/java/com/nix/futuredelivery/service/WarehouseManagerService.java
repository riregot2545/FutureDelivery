package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import org.springframework.stereotype.Service;

import java.util.List;

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
        warehouseManagerRepository.save(manager);
    }
}
