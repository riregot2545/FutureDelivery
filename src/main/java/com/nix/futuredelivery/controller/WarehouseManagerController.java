package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.service.WarehouseManagerService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
@RequestMapping("/warehouse_manager")
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }

    @GetMapping("/product_lines")
    public List<WarehouseProductLine> getProductLines(Authentication authentication) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return warehouseManagerService.getProductLines(user.getId());
    }

    @PostMapping("/warehouse")
    public void registrateWarehouse(@RequestBody Warehouse warehouse){
        warehouseManagerService.saveWarehouse(warehouse);
    }

    @PostMapping("/product_lines")
    public void addProductLine(@RequestBody List<Product> productLines, Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        if(!warehouseManagerService.hasWarehouse(user.getId())) throw new IllegalArgumentException("Managet with id "+user.getId()+" has no registered warehouse");
        warehouseManagerService.saveProductLines(productLines, user.getId());
    }

    @PatchMapping("/product_lines")
    public void editProductLine(@RequestBody List<WarehouseProductLine> productLines, Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        if(!warehouseManagerService.hasWarehouse(user.getId())) throw new IllegalArgumentException("Managet with id "+user.getId()+" has no registered warehouse");
        warehouseManagerService.editProductLines(productLines, user.getId());
    }

}
