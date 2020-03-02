package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.projections.WarehouseProductLinesOnly;
import com.nix.futuredelivery.security.MyUserPrincipal;
import com.nix.futuredelivery.service.WarehouseManagerService;
import lombok.Data;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Data
@RestController
@PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
@RequestMapping("/warehouse_manager")
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }

    @GetMapping("/get_product_line")
    public WarehouseProductLinesOnly getProductLine(WarehouseManager manager) {
        return warehouseManagerService.getProductLines(manager);
    }

    @PostMapping("/registration")
    public void registrateWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
        warehouseManagerService.saveWarehouseManager(warehouseManager);
    }

    @PostMapping("registrate_warehouse")
    public void registrateWarehouse(@RequestBody Warehouse warehouse){
        warehouseManagerService.saveWarehouse(warehouse);
    }

    @PostMapping("/add_product_lines")
    public void addProductLine(@RequestBody List<WarehouseProductLine> productLines, Authentication authentication){

        SystemUser user = (SystemUser) authentication.getPrincipal();
        WarehouseManager manager = warehouseManagerService.getManagerById(user.getId());
        warehouseManagerService.saveProductLines(productLines, user.getId());
    }


    @GetMapping("/private")
    public String getMessage(Authentication authentication) {
        return "yyyes";
    }
}
