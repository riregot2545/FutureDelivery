package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.exceptions.InvalidDeliveryOrderException;
import com.nix.futuredelivery.exceptions.NoRouteFoundException;
import com.nix.futuredelivery.exceptions.SomeWaybillsNotFoundException;
import com.nix.futuredelivery.repository.*;
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
    private StoreOrderRepository orderRepository;

    public DriverService(DriverRepository driverRepository, RouteRepository routeRepository, WarehouseRepository warehouseRepository, WaybillRepository waybillRepository, StoreOrderRepository orderRepository) {
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
        this.warehouseRepository = warehouseRepository;
        this.waybillRepository = waybillRepository;
        this.orderRepository = orderRepository;
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

            route.getWaybillList().forEach(w -> w.getStoreOrder().setProductLines(null));
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
                    warehouseProductLine.ifPresent(line -> line.setQuantity(line.getQuantity() - productLine.getQuantity()));
                }
            }
            List<StoreOrder> affectedOrders = waybills.stream().map(Waybill::getStoreOrder).collect(Collectors.toList());
            for (StoreOrder order : affectedOrders) {
                List<Waybill> waybillsByStoreOrder = waybillRepository.findByStoreOrder(order);
                if (waybillsByStoreOrder
                        .stream()
                        .flatMap(w -> w.getProductLines().stream())
                        .allMatch(WaybillProductLine::isDelivered)) {
                    order.setClosed(true);
                }
            }

            warehouseRepository.save(warehouse);
            waybillRepository.saveAll(waybills);
            orderRepository.saveAll(affectedOrders);

        } else
            throw new NoRouteFoundException(waybills.get(0).getRoute().getId());
    }
}
