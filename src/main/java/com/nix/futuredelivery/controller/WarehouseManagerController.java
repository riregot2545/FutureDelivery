package com.nix.futuredelivery.controller;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoStationException;
import com.nix.futuredelivery.service.WarehouseManagerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/warehouse_manager")
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }

    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @GetMapping("/product_lines")
    public List<WarehouseProductLine> getProductLines(Authentication authentication) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return warehouseManagerService.getProductLines(user.getId());
    }

    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PostMapping("/warehouse")
    public void registrateWarehouse(@RequestBody Warehouse warehouse) throws InterruptedException, ApiException, IOException {
        warehouseManagerService.saveWarehouse(warehouse);
    }

    @PostMapping()
    public void registerWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
        warehouseManagerService.saveWarehouseManager(warehouseManager);
    }

    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PostMapping("/product_lines")
    public void addProductLine(@RequestBody List<Product> productLines, Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        if(!warehouseManagerService.hasWarehouse(user.getId())) throw new NoStationException(user.getId());
        warehouseManagerService.saveProductLines(productLines, user.getId());
    }

    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PatchMapping("/product_lines")
    public void editProductLine(@RequestBody List<WarehouseProductLine> productLines, Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        if(!warehouseManagerService.hasWarehouse(user.getId())) throw new NoStationException(user.getId());
        warehouseManagerService.editProductLines(productLines, user.getId());
    }

}
