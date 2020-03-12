package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.security.MyUserDetailsService;

import com.nix.futuredelivery.service.RegistrationService;
import com.nix.futuredelivery.service.StoreManagerService;
import com.nix.futuredelivery.service.WarehouseManagerService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/warehouse_manager")
    public void registrateWarehouseManager(@RequestBody WarehouseManager warehouseManager) {
        registrationService.saveWarehouseManager(warehouseManager);
    }

    @PostMapping("/store_manager")
    public void registrateStoreManager(@RequestBody StoreManager storeManager) {
        registrationService.saveStoreManager(storeManager);
    }


}
