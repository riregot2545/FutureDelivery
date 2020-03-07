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
    private ProductRepository productRepository;
    private WarehouseRepository  warehouseRepository;
    public WarehouseManagerService(WarehouseManagerRepository warehouseManagerRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository){
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }
    @Transactional
    public List<WarehouseProductLine> getProductLines(Long id){
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(()->new IllegalStateException("no"));;
        Warehouse warehouse = manager.getWarehouse();
        return warehouse.getProductLines();
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
    public void saveProductLines(List<Product> lines, Long id){
        WarehouseManager manager = warehouseManagerRepository.findById(id).orElseThrow(()->new IllegalArgumentException("no"));

        Warehouse warehouse = manager.getWarehouse();
        List<WarehouseProductLine> productLines = new ArrayList<>();

        for(int i = 0; productLines.size()!= lines.size(); i++){
            Product p = productRepository.findById(lines.get(i).getId()).orElseThrow(()->new IllegalArgumentException("no"));
            WarehouseProductLine productLine = new WarehouseProductLine(warehouse);
            productLine.setProduct(p);
            productLines.add(productLine);
        }
        warehouse.getProductLines().addAll(productLines);
    }

    public void saveWarehouse(Warehouse warehouse){
        warehouseRepository.save(warehouse);
    }
}
