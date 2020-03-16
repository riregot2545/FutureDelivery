package com.nix.futuredelivery;

import com.nix.futuredelivery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleaner {
    @Autowired
    private DistanceRepository distanceRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private StoreOrderRepository orderRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private WaybillRepository waybillRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Transactional
    public void clearAll() {
        waybillRepository.deleteAll();
        routeRepository.deleteAll();
        driverRepository.deleteAll();
        carRepository.deleteAll();
        orderRepository.deleteAll();
        distanceRepository.deleteAll();
        warehouseRepository.deleteAll();
        storeRepository.deleteAll();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
    }
}
