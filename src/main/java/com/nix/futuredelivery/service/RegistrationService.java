package com.nix.futuredelivery.service;

import com.nix.futuredelivery.repository.StoreManagerRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private PasswordEncoder passwordEncoder;
    private StoreManagerRepository storeManagerRepository;
    private WarehouseManagerRepository warehouseManagerRepository;

    public RegistrationService(PasswordEncoder passwordEncoder, StoreManagerRepository storeManagerRepository, WarehouseManagerRepository warehouseManagerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.storeManagerRepository = storeManagerRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
    }


}
