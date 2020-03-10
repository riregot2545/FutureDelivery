package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.model.AuthenticationRequest;
import com.nix.futuredelivery.model.AuthenticationResponse;
import com.nix.futuredelivery.security.MyUserDetailsService;

import com.nix.futuredelivery.service.StoreManagerService;
import com.nix.futuredelivery.service.WarehouseManagerService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private WarehouseManagerService warehouseManagerService;
    private StoreManagerService storeManagerService;

    public RegistrationController(WarehouseManagerService warehouseManagerService, StoreManagerService storeManagerService) {
        this.warehouseManagerService = warehouseManagerService;
        this.storeManagerService = storeManagerService;
    }

    @PostMapping("/warehouse_manager")
    public void registrateWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
        warehouseManagerService.saveWarehouseManager(warehouseManager);
    }

    @PostMapping("/store_manager")
    public void registrateStoreManager(@RequestBody StoreManager storeManager) {
        storeManagerService.saveStoreManager(storeManager);
    }


}
