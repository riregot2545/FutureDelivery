package com.nix.futuredelivery;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseRandomDataFiller {
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
    private PasswordEncoder passwordEncoder;
    private DatabaseDataGenerator generator = new DatabaseDataGenerator();

    @Autowired
    private DatabaseCleaner cleaner = new DatabaseCleaner();

    @BeforeEach
    @Transactional
    void clean() {
        cleaner.clearAll();
        TestTransaction.flagForCommit();
    }

    private void encode(List<? extends SystemUser> users) {
        users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
    }

    @Test
    @Transactional
    void randomSmallDataTest() {
        ProductCategory productCategory = new ProductCategory(null, "testCategory", null);
        List<Car> cars = generator.cars(5, 4, 1000D, 1.1,
                15, 0.001);
        List<Driver> drivers = generator.drivers(15);
        encode(drivers);
        List<Store> stores = generator.storeAndManagers(20);
        List<Warehouse> warehouses = generator.warehouseAndManagers(5);
        List<Distance> distances = generator.distances(warehouses, stores);
        List<Product> products = generator.products(100, 0.1, 5, productCategory);
        List<StoreOrder> orders = generator.orders(20, stores, products, 2,
                10, 100);
        warehouses = generator.warehouseStock(warehouses, orders, 0.3);

        //Order depend!
        productCategoryRepository.save(productCategory);
        productRepository.saveAll(products);
        carRepository.saveAll(cars);
        driverRepository.saveAll(drivers);
        storeRepository.saveAll(stores);
        orderRepository.saveAll(orders);
        warehouseRepository.saveAll(warehouses);
        distanceRepository.saveAll(distances);

        TestTransaction.flagForCommit();

    }

    @Test
    @Transactional
    void randomMediumDataTest() {
        ProductCategory productCategory = new ProductCategory(null, "testCategory", null);
        List<Car> cars = generator.cars(10, 10, 1000D, 1.1,
                15, 0.001);
        List<Driver> drivers = generator.drivers(30);
        encode(drivers);
        List<Store> stores = generator.storeAndManagers(40);
        List<Warehouse> warehouses = generator.warehouseAndManagers(10);
        List<Distance> distances = generator.distances(warehouses, stores);
        List<Product> products = generator.products(200, 0.1, 10, productCategory);
        List<StoreOrder> orders = generator.orders(50, stores, products, 2,
                10, 400);
        warehouses = generator.warehouseStock(warehouses, orders, 0.3);

        //Order depend!
        productCategoryRepository.save(productCategory);
        productRepository.saveAll(products);
        carRepository.saveAll(cars);
        driverRepository.saveAll(drivers);
        storeRepository.saveAll(stores);
        orderRepository.saveAll(orders);
        warehouseRepository.saveAll(warehouses);
        distanceRepository.saveAll(distances);

        TestTransaction.flagForCommit();

    }
}
