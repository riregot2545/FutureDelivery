package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.repository.DriverRepository;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.repository.WaybillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private DriverRepository driverRepository;
    private RouteRepository routeRepository;
    private WarehouseRepository warehouseRepository;
    private WaybillRepository waybillRepository;

    public DriverService(DriverRepository driverRepository, RouteRepository routeRepository, WarehouseRepository warehouseRepository, WaybillRepository waybillRepository) {
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
        this.waybillRepository = waybillRepository;
    }

    @Transactional
    public List<Route> getDriversRoutes(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new IllegalStateException("no"));
        ;
        return driver.getDriversRoutes;
    }

    @Transactional
    public void saveDriversRoute(Driver driver) {
        String password = driver.getPassword();
        driver.setPassword("{noop}" + password);
        driverRepository.save(driver);
    }

    @Transactional
    public void checkDriversRoute(List<WaybillProductLine> waybillProductLines) throws Exception {
        List<Long> waybillKeys = waybillProductLines.stream().map(w -> w.getWaybill().getId()).distinct().collect(Collectors.toList());
        List<Waybill> waybills = waybillRepository.findAllById(waybillKeys);
        Optional<Route> routeOptional = routeRepository.findById(waybills.get(0).getRoute().getId());
        if (routeOptional.isPresent()) {
            Route route = routeOptional.get();

            Map<Integer, List<Waybill>> collectedWaybillsByQueuePlace = route.getWaybillList().stream().collect(Collectors.groupingBy(Waybill::getDeliveryQueuePlace));

            if (waybills.get(0).getDeliveryQueuePlace() != 0) {
                List<Waybill> previousWaybillsToCheck = collectedWaybillsByQueuePlace.get(waybills.get(0).getDeliveryQueuePlace() - 1);
                if (!previousWaybillsToCheck.get(0).getProductLines().get(0).isDelivered()) {
                    // TODO: 14.03.2020 Make normal Server exception
                    throw new Exception();
                }

                Warehouse warehouse = route.getWarehouse();
                for (Waybill waybill : previousWaybillsToCheck) {
                    for (WaybillProductLine productLine : waybill.getProductLines()) {
                        productLine.setDelivered(true);

                        Optional<WarehouseProductLine> warehouseProductLine = warehouse.getProductLines()
                                .stream()
                                .filter(w -> w.getProduct().equals(productLine.getProduct()))
                                .findFirst();
                        warehouseProductLine.get().setQuantity(warehouseProductLine.get().getQuantity() - productLine.getQuantity());
                    }
                }
                warehouseRepository.save(warehouse);
                waybillRepository.saveAll(waybills);
            }


        }
        // TODO: 14.03.2020 Make normal Server exception
        throw new Exception();
    }
}
