package com.nix.futuredelivery.service;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.StoreRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceService {
    private GoogleMapsService mapsService;
    private DistanceRepository distanceRepository;

    public DistanceService(GoogleMapsService mapsService, DistanceRepository distanceRepository, StoreRepository storeRepository) {
        this.mapsService = mapsService;
        this.distanceRepository = distanceRepository;
        this.storeRepository = storeRepository;
    }

    private StoreRepository storeRepository;

    @Async
    public void addNewPoint(Address address) throws InterruptedException, ApiException, IOException {
        List<Distance> distances = new ArrayList<>();
        List<Store> stores = storeRepository.findAll();
        for (Store store : stores) {
            Address storeAddress = store.getAddress();
            double distanceBetweenCords = mapsService.getDistanceBetweenCords(address, storeAddress);
            distances.add(new Distance(address, storeAddress, distanceBetweenCords));
            distances.add(new Distance(storeAddress, address, distanceBetweenCords));
        }

        distanceRepository.saveAll(distances);
    }
}
