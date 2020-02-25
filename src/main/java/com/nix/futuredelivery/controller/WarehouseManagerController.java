package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import com.nix.futuredelivery.service.WarehouseManagerService;
import lombok.Data;
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
    @PostMapping("/save_manager")
    public void saveManager(@RequestBody String login){
        Warehouse warehouse = new Warehouse();
        WarehouseManager manager = new WarehouseManager(warehouse);
        manager.setLogin(login);
        warehouse.setWarehouseManager(manager);
        warehouseManagerService.saveWarehouseManager(manager);
    }
}
