package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.StoreManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreManagerService {

    private StoreManagerRepository storeManagerRepository;

    public StoreManagerService(StoreManagerRepository storeManagerRepository) {
        this.storeManagerRepository = storeManagerRepository;
    }

    @Transactional
    public void saveStoreManager(StoreManager manager){
        String password = manager.getPassword();
        manager.setPassword("{noop}"+password);
        storeManagerRepository.save(manager);
    }
}
