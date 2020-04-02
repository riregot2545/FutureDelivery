package com.nix.futuredelivery.controller;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.InvalidRequestEntityException;
import com.nix.futuredelivery.exceptions.NoStationException;
import com.nix.futuredelivery.service.WarehouseManagerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/warehouse_manager")
public class WarehouseManagerController {
    private WarehouseManagerService warehouseManagerService;

    public WarehouseManagerController(WarehouseManagerService warehouseManagerService) {
        this.warehouseManagerService = warehouseManagerService;
    }

    @ApiOperation(value = "View product lines of the warehouse")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @GetMapping("/product_lines")
    public List<WarehouseProductLine> getProductLines(Authentication authentication) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return warehouseManagerService.getProductLines(user.getId());
    }

    @ApiOperation(value = "Register a warehouse")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PostMapping("/warehouse")
    public void registerWarehouse(@Valid @RequestBody Warehouse warehouse, BindingResult bindingResult) throws InterruptedException, ApiException, IOException {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            warehouseManagerService.saveWarehouse(warehouse);
        }
    }

    @ApiOperation(value = "Register new warehouse manager")
    @PostMapping()
    public ResponseEntity<String> registerWarehouseManager(@Valid @RequestBody WarehouseManager warehouseManager, BindingResult bindingResult) {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            Long id = warehouseManagerService.saveWarehouseManager(warehouseManager);
            return new ResponseEntity<>("Warehouse manager saved successfully with id " + id, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Fill warehouse product lines")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PostMapping("/product_lines")
    public void addProductLine(@Valid @RequestBody List<Product> productLines, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            SystemUser user = (SystemUser) authentication.getPrincipal();
            if (!warehouseManagerService.hasWarehouse(user.getId())) throw new NoStationException(user.getId());
            warehouseManagerService.saveProductLines(productLines, user.getId());
        }
    }

    @ApiOperation(value = "Edit existing warehouse product lines and add new if needed")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    @PatchMapping("/product_lines")
    public void editProductLine(@Valid @RequestBody List<WarehouseProductLine> productLines, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.getErrorCount() > 0)
            throw new InvalidRequestEntityException(bindingResult.getAllErrors());
        else {
            SystemUser user = (SystemUser) authentication.getPrincipal();
            if (!warehouseManagerService.hasWarehouse(user.getId())) throw new NoStationException(user.getId());
            warehouseManagerService.editProductLines(productLines, user.getId());
        }
    }

}
