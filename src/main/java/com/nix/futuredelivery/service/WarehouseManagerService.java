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
    private WarehouseRepository  warehouseRepository;
    public WarehouseManagerService(WarehouseManagerRepository warehouseManagerRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository){
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }
    @Transactional
    public WarehouseProductLinesOnly getProductLines(WarehouseManager manager){
        return warehouseRepository.findProductLinesByWarehouseManager(manager);
    }
    @Transactional
    public void saveWarehouseManager(WarehouseManager manager){
        String password = manager.getPassword();
        manager.setPassword("{noop}"+password);
        warehouseManagerRepository.save(manager);
    }
    @Transactional
    public WarehouseManager getManagerById(Long id){
        return warehouseManagerRepository.findById(id).orElseThrow(()->new IllegalStateException("no"));
    }

    @Transactional
    public void saveProductLines(List<WarehouseProductLine> lines, Long id){
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(()->new IllegalArgumentException("no"));

        Warehouse warehouse = manager.getWarehouse();
        //Warehouse warehouse = warehouseRepository.findByWarehouseManager(manager).orElseThrow(()->new IllegalArgumentException("no"));;
        warehouse.getProductLines().addAll(lines);
    }

    public void saveWarehouse(Warehouse warehouse){
        warehouseRepository.save(warehouse);
    }
}
