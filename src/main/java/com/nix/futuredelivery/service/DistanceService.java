package com.nix.futuredelivery.service;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.Distance;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.exceptions.DistanceNotFoundException;
import com.nix.futuredelivery.exceptions.InvalidAddressException;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.StoreRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DistanceService {
    private GoogleMapsService mapsService;
    private DistanceRepository distanceRepository;
    private StoreRepository storeRepository;
    private WarehouseRepository warehouseRepository;

    public DistanceService(GoogleMapsService mapsService, DistanceRepository distanceRepository, StoreRepository storeRepository, WarehouseRepository warehouseRepository) {
        this.mapsService = mapsService;
        this.distanceRepository = distanceRepository;
        this.storeRepository = storeRepository;
        this.warehouseRepository = warehouseRepository;
    }


    @Async
    public void addNewPoint(Address address) throws InterruptedException, ApiException, IOException {
        log.info("New transition point added. Calculating distances...");
        List<Distance> distances = new ArrayList<>();
        List<Store> stores = storeRepository.findAll();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        boolean isPointLocation = true;

        if (address.getPointLocation() == null) {
            if (!address.getAddressLine1().isEmpty() && !address.getCity().isEmpty() &&
                    !address.getRegion().isEmpty() && !address.getCountry().isEmpty()) {
                isPointLocation = false;
            } else
                throw new InvalidAddressException(address);
        }

        for (Store store : stores) {
            Address storeAddress = store.getAddress();
            double distanceBetweenCords = 0;
            try {
                if (isPointLocation)
                    distanceBetweenCords = mapsService.getDistanceBetweenCords(address, storeAddress);
                else
                    distanceBetweenCords = mapsService.getDistanceBetweenNativeAddress(address, storeAddress);


                distances.add(new Distance(address, storeAddress, distanceBetweenCords));
                distances.add(new Distance(storeAddress, address, distanceBetweenCords));
            } catch (DistanceNotFoundException e) {
                log.warn(e.getMessage());
            }

        }
        for (Warehouse warehouse : warehouses) {
            Address warehouseAddress = warehouse.getAddress();
            double distanceBetweenCords = 0;
            try {
                if (isPointLocation)
                    distanceBetweenCords = mapsService.getDistanceBetweenCords(address, warehouseAddress);
                else
                    distanceBetweenCords = mapsService.getDistanceBetweenNativeAddress(address, warehouseAddress);

                distances.add(new Distance(address, warehouseAddress, distanceBetweenCords));
                distances.add(new Distance(warehouseAddress, address, distanceBetweenCords));
            } catch (DistanceNotFoundException e) {
                log.warn(e.getMessage());
            }
        }


        distanceRepository.saveAll(distances);
        log.info("Calculating distances has been done.");
    }
}
