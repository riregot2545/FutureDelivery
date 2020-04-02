package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.exceptions.InvalidRequestEntityException;
import com.nix.futuredelivery.service.AdministratorService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdministratorController {

    AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @ApiOperation(value = "View all active routes")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/active_routes")
    public List<Route> getActiveRoutes() {
        return administratorService.getActiveRoutes();
    }

    @ApiOperation(value = "View warehouses state (all information about warehouse)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/warehouses_state")
    public List<Warehouse> getWarehousesState() {
        return administratorService.getWarehousesState();
    }

    @ApiOperation(value = "Get notification about unconfirmed products or users")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/notification")
    public Notification getNotification() {
        return administratorService.getNotification();
    }

    @ApiOperation(value = "View unconfirmed products")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/unconfirmed_products")
    public List<Product> getUnconfirmedProducts() {
        return administratorService.getUnconfirmedProducts();
    }

    @ApiOperation(value = "View unconfirmed store managers")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/unconfirmed_store_manager")
    public List<StoreManager> getUnconfirmedStoreManager() {
        return administratorService.getUnconfirmedStoreManagers();
    }

    @ApiOperation(value = "View unconfirmed warehouse managers")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/unconfirmed_warehouse_manager")
    public List<WarehouseManager> getUnconfirmedWarehouseManager() {
        return administratorService.getUnconfirmedWarehouseManagers();
    }

    @ApiOperation(value = "View undistributed orders")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("new_store_orders")
    public List<StoreOrder> getNewStoreOrders() {
        return administratorService.getUndistributedOrders();
    }

    @ApiOperation(value = "Confirm products")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/confirm_product")
    public void confirmProduct(@RequestBody List<Product> productList) {
        administratorService.confirmProducts(productList);
    }

    @ApiOperation(value = "Confirm store managers")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/confirm_store_managers")
    public void confirmStoreManagers(@RequestBody List<StoreManager> storeManagerList) {
        administratorService.confirmStoreManagers(storeManagerList);
    }

    @ApiOperation(value = "Confirm warehouse managers")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/confirm_warehouse_managers")
    public void confirmWarehouseManagers(@RequestBody List<WarehouseManager> warehouseManagerList) {
        administratorService.confirmWarehouseManagers(warehouseManagerList);
    }

    @ApiOperation(value = "Register new driver")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register_driver")
    public void registerDriver(@Valid @RequestBody List<Driver> driverList, BindingResult bindingResult) {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            administratorService.addNewDriver(driverList);
        }
    }

    @ApiOperation(value = "Add new car")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/new_car")
    public void addNewCar(@Valid @RequestBody List<Car> carList, BindingResult bindingResult) {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            administratorService.addNewCar(carList);
        }
    }


}
