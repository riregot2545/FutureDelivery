package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderStatus;
import com.nix.futuredelivery.exceptions.NoPersonException;
import com.nix.futuredelivery.exceptions.NoProductException;
import com.nix.futuredelivery.repository.*;
import com.nix.futuredelivery.entity.Notification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministratorService {
    private RouteRepository routeRepository;
    private WarehouseRepository warehouseRepository;
    private WarehouseManagerRepository warehouseManagerRepository;
    private StoreManagerRepository storeManagerRepository;
    private ProductRepository productRepository;
    private PasswordEncoder passwordEncoder;
    private DriverRepository driverRepository;
    private CarRepository carRepository;
    private StoreOrderRepository storeOrderRepository;
    private AdminRepository adminRepository;


    public AdministratorService(RouteRepository routeRepository, WarehouseRepository warehouseRepository, WarehouseManagerRepository warehouseManagerRepository, StoreManagerRepository storeManagerRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder, DriverRepository driverRepository, CarRepository carRepository, StoreOrderRepository storeOrderRepository) {
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
        this.storeManagerRepository = storeManagerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.driverRepository = driverRepository;
        this.carRepository = carRepository;
        this.storeOrderRepository = storeOrderRepository;
    }

    public List<Route> getActiveRoutes() {
        return routeRepository.findByClosedFalse();
    }

    public List<Warehouse> getWarehousesState() {
        return warehouseRepository.findAll();
    }

    public Notification getNotification() {
        List<Product> products = productRepository.findByIsConfirmedFalse();
        List<WarehouseManager> warehouseManagers = warehouseManagerRepository.findByIsConfirmedFalse();
        List<StoreManager> storeManagers = storeManagerRepository.findByIsConfirmedFalse();
        Notification notification;
        if (products.isEmpty() && warehouseManagers.isEmpty() &&
                storeManagers.isEmpty()) {
            notification = new Notification("Nothing to validate!", true);
        } else {
            notification = new Notification("Have an unvalidated users or products!", false);

        }
        return notification;
    }

    public List<Product> getUnconfirmedProducts() {
        return productRepository.findByIsConfirmedFalse();
    }

    public List<StoreManager> getUnconfirmedStoreManagers() {
        return storeManagerRepository.findByIsConfirmedFalse();
    }

    public List<WarehouseManager> getUnconfirmedWarehouseManagers() {
        return warehouseManagerRepository.findByIsConfirmedFalse();
    }

    @Transactional
    public void confirmProducts(List<Product> productList) {
        for (Product p : productList) {
            Long id = p.getId();
            p = productRepository.findById(id).orElseThrow(() -> new NoProductException(id));
            p.setConfirmed(true);
            productRepository.save(p);
        }

    }

    @Transactional
    public void confirmStoreManagers(List<StoreManager> storeManagers) {
        for (StoreManager storeManager : storeManagers) {
            Long id = storeManager.getId();
            storeManager = storeManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Store manager", id));
            storeManager.setConfirmed(true);
            storeManagerRepository.save(storeManager);
        }
    }

    @Transactional
    public void confirmWarehouseManagers(List<WarehouseManager> warehouseManagerList) {
        for (WarehouseManager warehouseManager : warehouseManagerList) {
            Long id = warehouseManager.getId();
            warehouseManager = warehouseManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Warehouse manager", id));
            warehouseManager.setConfirmed(true);
            warehouseManagerRepository.save(warehouseManager);
        }
    }

    @Transactional
    public void addNewDriver(List<Driver> driverList) {
        for (Driver driver : driverList) {
            String password = driver.getPassword();
            driver.setPassword(passwordEncoder.encode(password));
            driverRepository.save(driver);
        }
    }

    @Transactional
    public void addNewCar(List<Car> carList) {
        for (Car car : carList) {
            carRepository.save(car);
        }
    }

    @Transactional
    public List<StoreOrder> getUndistributedOrders() {
        return storeOrderRepository.findByOrderStatus(OrderStatus.NEW);
    }
}

