package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import com.nix.futuredelivery.service.WarehouseManagerService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Data
@RestController
@RequestMapping("/warehouse_manager")
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }
    @GetMapping("/get_product_line")
    public WarehouseProductLinesOnly getProductLine(WarehouseManager manager){
        return warehouseManagerService.getProductLines(manager);
    }

    @PostMapping("/registration")
    public void registr(@RequestBody WarehouseManager warehouseManager) {
        warehouseManagerService.saveWarehouseManager(warehouseManager);
    }

    @GetMapping("/private")
    public String getMessage(Authentication authentication) {
        System.out.println(authentication.toString());
        return "Hello from private API controller";
    }
}
