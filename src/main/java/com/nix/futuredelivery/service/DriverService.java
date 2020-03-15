package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.repository.DriverRepository;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.repository.WaybillRepository;
import com.nix.futuredelivery.service.exceptions.InvalidDeliveryOrderException;
import com.nix.futuredelivery.service.exceptions.NoRouteFoundException;
import com.nix.futuredelivery.service.exceptions.SomeWaybillsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new IllegalStateException("Driver with id=" + id + " is not exist."));
        List<Route> routes = routeRepository.findByDriverAndIsClosedFalse(driver);
        for (Route route : routes) {
            List<Store> stores = route.getWaybillList()
                    .stream()
                    .sorted(Comparator.comparingInt(Waybill::getDeliveryQueuePlace))
                    .map(w -> w.getStoreOrder().getStore())
                    .distinct()
                    .collect(Collectors.toList());
            route.setRoutePoints(stores);
        }
        return routes;
    }

    @Transactional
    public void checkCompletedDelivery(List<Waybill> completedWaybills) {
        List<Long> waybillKeys = completedWaybills.stream().mapToLong(Waybill::getId).boxed().collect(Collectors.toList());
        List<Waybill> waybills = waybillRepository.findAllById(waybillKeys);
        if (waybills.size() != completedWaybills.size()) {
            List<Long> foundKeys = waybills.stream().mapToLong(Waybill::getId).boxed().collect(Collectors.toList());
            List<Long> differ = waybillKeys.stream().filter(orig -> !foundKeys.contains(orig)).collect(Collectors.toList());
            throw new SomeWaybillsNotFoundException(differ);
        }
        Optional<Route> routeOptional = routeRepository.findById(waybills.get(0).getRoute().getId());
        if (routeOptional.isPresent()) {
            Route route = routeOptional.get();

            Map<Integer, List<Waybill>> collectedWaybillsByQueuePlace = route.getWaybillList().stream().collect(Collectors.groupingBy(Waybill::getDeliveryQueuePlace));

            if (waybills.get(0).getDeliveryQueuePlace() != 0) {
                List<Waybill> previousWaybillsToCheck = collectedWaybillsByQueuePlace.get(waybills.get(0).getDeliveryQueuePlace() - 1);
                if (!previousWaybillsToCheck.get(0).getProductLines().get(0).isDelivered()) {
                    throw new InvalidDeliveryOrderException(previousWaybillsToCheck.get(0).getDeliveryQueuePlace(),
                            waybills.get(0).getDeliveryQueuePlace());
                }
            }

            Warehouse warehouse = route.getWarehouse();
            for (Waybill waybill : waybills) {
                for (WaybillProductLine productLine : waybill.getProductLines()) {
                    productLine.setDelivered(true);

                    Optional<WarehouseProductLine> warehouseProductLine = warehouse.getProductLines()
                            .stream()
                            .filter(w -> w.getProduct().equals(productLine.getProduct()))
                            .findFirst();
                    if (!warehouseProductLine.isPresent())


                        warehouseProductLine.get().setQuantity(warehouseProductLine.get().getQuantity() - productLine.getQuantity());
                }
            }
            warehouseRepository.save(warehouse);
            waybillRepository.saveAll(waybills);

        }
        throw new NoRouteFoundException(waybills.get(0).getRoute().getId());
    }
}
