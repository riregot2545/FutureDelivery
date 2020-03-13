package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DriverLoad;
import com.nix.futuredelivery.transportation.model.WarehouseKeyListGroup;
import com.nix.futuredelivery.transportation.psolver.CarAssigner;
import com.nix.futuredelivery.transportation.psolver.PolarDistributionSolver;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TransportationAssigner {
    private final CarAssigner carAssigner;
    private final Queue<DriverLoad> drivers;
    private final List<WarehouseKeyListGroup> mappedWarehouses;
    private final List<Route> routes;
    private Car currentCar;
    private List<StationPoint> tackedStationPoints;
    private Map<StoreOrder, Waybill> waybillsByOrderMap;

    private StationPoint currentPoint;

    public TransportationAssigner(List<Car> cars, Queue<DriverLoad> drivers, List<DistributionEntry> distributionEntries) {
        this.carAssigner = new CarAssigner(cars);
        this.mappedWarehouses = groupEntriesByWarehouse(distributionEntries);
        this.drivers = drivers;
        this.routes = new ArrayList<>();
        this.tackedStationPoints = new ArrayList<>();
        this.waybillsByOrderMap = new HashMap<>();
    }

    public List<Route> assign() {
        log.info("Starting route creation and car assigning. Warehouse count {}.", mappedWarehouses.size());
        for (WarehouseKeyListGroup warehouseGroup : mappedWarehouses) {
            Warehouse warehouse = warehouseGroup.getKey();
            List<DistributionEntry> entries = warehouseGroup.getList();

            Map<Store, List<DistributionEntry>> storeListMap = groupEntriesByStore(entries);
            PolarDistributionSolver polarSolver = new PolarDistributionSolver(warehouse, storeListMap.keySet());

            List<StationPoint> stationPoints = polarSolver.resolvePolar();
            List<List<DistributionEntry>> lists = new ArrayList<>(storeListMap.values());
            for (int i = 0; i < lists.size(); i++) {
                StationPoint stationPoint = stationPoints.get(i);
                List<AssignOrderLine> collectedOrderLines = getOrderLinesFromEntryList(lists.get(i));
                stationPoint.setProductLines(collectedOrderLines);
                stationPoint.setAllProductsVolume();
                stationPoints.add(stationPoint);
            }

            currentCar = carAssigner.getNextMostFreeCar();

            for (int i = 0; i < stationPoints.size(); i++) {
                currentPoint = stationPoints.get(i);
                for (AssignOrderLine productLine : currentPoint.getProductLines()) {

                    if (productLine.getRemainQuantity() > 0) {
                        double oneProductVolume = productLine.getProduct().getVolume().getVolume();
                        if (currentCar.getFreeVolume() >= oneProductVolume) {
                            appendLineToWaybill(productLine);
                        } else {
                            if (tackedStationPoints.size() < 2) {
                                Optional<Capacity> groupIncrementResult = carAssigner.incrementGroupLevel();
                                if (groupIncrementResult.isPresent()) {
                                    carAssigner.resetAssignCar(currentCar);
                                    currentCar = carAssigner.getNextMostFreeCar(false);
                                    i = -1;
                                    resetAssignFromTacked();
                                    resetCache();
                                    break;
                                } else {
                                    addNewRoute(warehouse);
                                    i = -1;
                                    break;
                                }
                            } else {
                                addNewRoute(warehouse);
                                i = -1;
                                break;
                            }
                        }
                    }
                }

                if ((i + 1) >= stationPoints.size() && currentPoint.hasDemand())
                    i -= 2;
            }
            addNewRoute(warehouse);
        }
        log.info("Successfully ended route creation. Created {} routes.", routes.size());
        return routes;
    }


    private void appendLineToWaybill(AssignOrderLine productLine) {
        StoreOrder order = productLine.getStoreOrder();
        double oneProductVolume = productLine.getProduct().getVolume().getVolume();
        int possibleQuantity = (int) (currentCar.getFreeVolume() / oneProductVolume);
        int acceptedQuantity = Math.min(possibleQuantity, productLine.getRemainQuantity());


        if (waybillsByOrderMap.get(order) == null)
            waybillsByOrderMap.put(order, new Waybill(null, order, new ArrayList<>(), null, 0, null, null));

        WaybillProductLine waybillLine = new WaybillProductLine();
        waybillLine.setWaybill(waybillsByOrderMap.get(order));
        waybillLine.setProduct(productLine.getProduct());
        waybillLine.setQuantity(acceptedQuantity);
        waybillsByOrderMap.get(order).getProductLines().add(waybillLine);

        productLine.addAssignQuantity(acceptedQuantity);

        currentCar.fillVolume(new Volume(waybillLine.getQuantity() *
                waybillLine.getProduct().getVolume().getVolume()));

        if (!tackedStationPoints.contains(currentPoint))
            tackedStationPoints.add(currentPoint);
    }

    private void addNewRoute(Warehouse warehouse) {
        Route route = new Route(null,
                getNextDriver(),
                currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                warehouse);
        route.getWaybillList().forEach(w -> {
            w.setRoute(route);
            w.updateProductCost();
        });
        routes.add(route);
        currentCar.resetFullness();
        currentCar = carAssigner.getNextMostFreeCar();
        resetCache();
    }

    private void resetCache() {
        waybillsByOrderMap.clear();
        tackedStationPoints.clear();
    }

    private void resetAssignFromTacked() {
        for (StationPoint tackedStationPoint : tackedStationPoints) {

            waybillsByOrderMap.forEach((order, waybill) -> {
                List<AssignOrderLine> originalLinesByOrder = tackedStationPoint.getProductLines()
                        .stream()
                        .filter(line -> line.getStoreOrder().equals(order))
                        .collect(Collectors.toList());
                if (originalLinesByOrder.isEmpty())
                    throw new IllegalStateException("Can't find waybill order " + order + " in original station orders");

                waybill.getProductLines().forEach(waybillProductLine -> {
                    Optional<AssignOrderLine> assignOrderLineOptional = originalLinesByOrder.stream()
                            .filter(line -> line.getProduct().equals(waybillProductLine.getProduct()))
                            .findFirst();

                    if (!assignOrderLineOptional.isPresent())
                        throw new IllegalStateException("Can't find waybill line product " + waybillProductLine.getProduct() + " in original product line");

                    assignOrderLineOptional.get().resetAssign(waybillProductLine.getQuantity());
                });
            });

        }
    }

    private List<AssignOrderLine> getOrderLinesFromEntryList(List<DistributionEntry> entries) {
        return entries.stream()
                .flatMap(e -> e.getOrderProductLines().stream())
                .map(ordLine -> new AssignOrderLine(ordLine.getProduct(), ordLine.getQuantity(),
                        ordLine.getStoreOrder(), 0))
                .collect(Collectors.toList());
    }

    private List<WarehouseKeyListGroup> groupEntriesByWarehouse(List<DistributionEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(e -> e.getDistributionKey().getWarehouse()))
                .entrySet()
                .stream()
                .map(e -> new WarehouseKeyListGroup(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private Driver getNextDriver() {
        DriverLoad driverLoad = drivers.poll();
        driverLoad.incrementLoad();
        drivers.add(driverLoad);
        return driverLoad.getDriver();
    }

    private Map<Store, List<DistributionEntry>> groupEntriesByStore(List<DistributionEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(e -> e.getDistributionKey().getStore()));
    }
}
