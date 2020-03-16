package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.service.AdministratorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdministratorController {

    AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }


    @GetMapping("/active_routes")
    public List<Route> getActiveRoutes() {
        return administratorService.getActiveRoutes();
    }

    @GetMapping("/warehouses_state")
    public List<Warehouse> getWarehousesState() {
        return administratorService.getWarehousesState();
    }

    @GetMapping("try")
    public String hasAccess(Authentication authentication) {
        return "yess";
    }

    @GetMapping("/notification")
    public Notification getNotidication() {
        return administratorService.getNotification();
    }

    @GetMapping("/unconfirmed_products")
    public List<Product> getUnconfirmedProducts() {
        return administratorService.getUnconfirmedProducts();
    }

    @GetMapping("/unconfirmed_store_manager")
    public List<StoreManager> getUnconfirmedStoreManager() {
        return administratorService.getUnconfirmedStoreManagers();
    }

    @GetMapping("/unconfirmed_warehouse_manager")
    public List<WarehouseManager> getUnconfirmedWarehouseManager() {
        return administratorService.getUnconfirmedWarehouseManagers();
    }

    @GetMapping("new_store_orders")
    public List<StoreOrder> getNewStoreOrders() {
        return administratorService.getUndistributedOrders();
    }

    @PostMapping("/confirm_product")
    public void confirmProduct(@RequestBody List<Product> productList) {
        administratorService.confirmProducts(productList);
    }

    @PostMapping("/confirm_store_managers")
    public void confirmStoreManagers(@RequestBody List<StoreManager> storeManagerList) {
        administratorService.confirmStoreManagers(storeManagerList);
    }

    @PostMapping("/confirm_warehouse_managers")
    public void confirmWarehouseManagers(@RequestBody List<WarehouseManager> warehouseManagerList) {
        administratorService.confirmWarehouseManagers(warehouseManagerList);
    }

    @PostMapping("/register_driver")
    public void registerDriver(@RequestBody List<Driver> driverList) {
        administratorService.addNewDriver(driverList);
    }

    @PostMapping("/new_car")
    public void addNewCar(@RequestBody List<Car> carList) {
        administratorService.addNewCar(carList);
    }


}
