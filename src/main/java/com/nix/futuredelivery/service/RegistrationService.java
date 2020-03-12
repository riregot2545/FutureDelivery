package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.StoreManagerRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void saveStoreManager(StoreManager manager){
        String password = manager.getPassword();
        manager.setPassword(passwordEncoder.encode(password));
        storeManagerRepository.save(manager);
    }

    @Transactional
    public void saveWarehouseManager(WarehouseManager manager) {
        String password = manager.getPassword();
        manager.setPassword(passwordEncoder.encode(password));
        warehouseManagerRepository.save(manager);
    }
}
