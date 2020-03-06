package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.WarehouseKeyListGroup;
import com.nix.futuredelivery.transportation.psolver.CarAssigner;
import com.nix.futuredelivery.transportation.psolver.PolarDistributionSolver;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;

import java.util.*;
import java.util.stream.Collectors;

public class TransportationAssigner {
    private final CarAssigner carAssigner;
    private final Queue<Driver> drivers;
    private final List<WarehouseKeyListGroup> mappedWarehouses;
    private final List<Route> routes;
    private Car currentCar;
    private List<StationPoint> tackedStationPoints;
    private Map<StoreOrder, Waybill> waybillsByOrderMap;

    private StationPoint currentPoint;

    public TransportationAssigner(List<Car> cars, List<DistributionEntry> distributionEntries, List<Driver> drivers) {
        this.carAssigner = new CarAssigner(cars);
        this.mappedWarehouses = groupEntriesByWarehouse(distributionEntries);
        this.drivers = new LinkedList<>(drivers);
        this.routes = new ArrayList<>();
        this.tackedStationPoints = new ArrayList<>();
        this.waybillsByOrderMap = new HashMap<>();
    }

    public List<Route> assign() {
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
                        StoreOrder order = productLine.getStoreOrder();

                        double oneProductVolume = productLine.getProduct().getVolume().getVolume();
                        if (currentCar.getFreeVolume() >= oneProductVolume) {
                            appendLineToWaybill(order, productLine);
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
        return routes;
    }


    private void appendLineToWaybill(StoreOrder order, AssignOrderLine productLine) {
        double oneProductVolume = productLine.getProduct().getVolume().getVolume();
        int possibleQuantity = (int) (currentCar.getFreeVolume() / oneProductVolume);
        int acceptedQuantity = Math.min(possibleQuantity, productLine.getRemainQuantity());


        if (waybillsByOrderMap.get(order) == null)
            waybillsByOrderMap.put(order, new Waybill(null, order, new ArrayList<>(), null));

        WaybillProductLine waybillLine =
                new WaybillProductLine(productLine.getProduct(), acceptedQuantity,
                        waybillsByOrderMap.get(order), false);
        waybillsByOrderMap.get(order).getProductLines().add(waybillLine);

        productLine.addAssignQuantity(acceptedQuantity);

        currentCar.fillVolume(new Volume(waybillLine.getQuantity() *
                waybillLine.getProduct().getVolume().getVolume()));

        if (!tackedStationPoints.contains(currentPoint))
            tackedStationPoints.add(currentPoint);
    }

    private void addNewRoute(Warehouse warehouse) {
        routes.add(new Route(null,
                getNextDriver(),
                currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                warehouse));
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
            for (AssignOrderLine line : tackedStationPoint.getProductLines()) {
                StoreOrder storeOrder = line.getStoreOrder();
                Waybill waybill = waybillsByOrderMap.get(storeOrder);
                if (line.getAssignQuantity() != 0 && waybill != null) {
                    List<WaybillProductLine> productLines =
                            waybill.getProductLines();
                    WaybillProductLine searchedLine = productLines.stream()
                            .filter(waybillProductLine -> waybillProductLine.getProduct().equals(line.getProduct()))
                            .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find product line"));

                    line.resetAssign(searchedLine.getQuantity());
                }
            }

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
        return drivers.poll();
    }

    private Map<Store, List<DistributionEntry>> groupEntriesByStore(List<DistributionEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(e -> e.getDistributionKey().getStore()));
    }
}
