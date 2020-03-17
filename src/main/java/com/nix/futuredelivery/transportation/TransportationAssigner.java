package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DriverAssignEntry;
import com.nix.futuredelivery.transportation.model.WarehouseEntryGroup;
import com.nix.futuredelivery.transportation.psolver.CarAssigner;
import com.nix.futuredelivery.transportation.psolver.PolarDistributionSolver;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TransportationAssigner {
    private final CarAssigner carAssigner;
    private final Queue<DriverAssignEntry> drivers;
    private final List<WarehouseEntryGroup> mappedWarehouses;
    private final List<Route> routes;
    private Car currentCar;
    private List<StationPoint> tackedStationPoints;
    private Map<StoreOrder, Waybill> waybillsByOrderMap;

    private StationPoint currentPoint;

    /**
     * Constructs default transportation assigner.
     *
     * @param cars                car list from repository used for assigning.
     * @param drivers             driver priority queue of assign entries.
     * @param distributionEntries list of entries received from {@code TransportationGrouper}.
     */
    public TransportationAssigner(List<Car> cars, Queue<DriverAssignEntry> drivers, List<DistributionEntry> distributionEntries) {
        this.carAssigner = new CarAssigner(cars);
        this.mappedWarehouses = groupEntriesByWarehouse(distributionEntries);
        this.drivers = drivers;
        this.routes = new ArrayList<>();
        this.tackedStationPoints = new ArrayList<>();
        this.waybillsByOrderMap = new HashMap<>();
    }

    /**
     * Entry point for assign. Algorithm iterate warehouses for route creation. First of all it retrieve station points
     * from distribution entries and sorts it using {@code PolarDistributionSolver}. Then algorithm set first level car and
     * try to fill it by station order lines. If car fulled to capacity, but route consists of one station only, algorithm
     * can increase car group level and take one with bigger capacity.
     * @return list of crated routes.
     */
    public List<Route> assign() {
        log.info("Starting route creation and car assigning. Warehouse count {}.", mappedWarehouses.size());
        for (WarehouseEntryGroup warehouseGroup : mappedWarehouses) {
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
                        double oneProductVolume = productLine.getProduct().getVolume().getVolumeWeight();
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

    /**
     * Appends new product line to waybill or create it, if waybills with this store order does not exists.
     * Fill place in car by product line. Changes assign quantity in line after filling. Add station to station list
     * {@code tackedStationPoints}, if it does not contain one.
     * @param productLine product line to append.
     */
    private void appendLineToWaybill(AssignOrderLine productLine) {
        StoreOrder order = productLine.getStoreOrder();
        double oneProductVolume = productLine.getProduct().getVolume().getVolumeWeight();
        int possibleQuantity = (int) (currentCar.getFreeVolume() / oneProductVolume);
        int acceptedQuantity = Math.min(possibleQuantity, productLine.getRemainQuantity());


        if (waybillsByOrderMap.get(order) == null)
            waybillsByOrderMap.put(order, new Waybill(null, order, new ArrayList<>(), null, 0, null, null, false));

        WaybillProductLine waybillLine = new WaybillProductLine();
        waybillLine.setWaybill(waybillsByOrderMap.get(order));
        waybillLine.setProduct(productLine.getProduct());
        waybillLine.setQuantity(acceptedQuantity);
        waybillsByOrderMap.get(order).getProductLines().add(waybillLine);

        productLine.addAssignQuantity(acceptedQuantity);

        currentCar.fillVolume(new Volume(waybillLine.getQuantity() *
                waybillLine.getProduct().getVolume().getVolumeWeight()));

        if (!tackedStationPoints.contains(currentPoint))
            tackedStationPoints.add(currentPoint);
    }

    /**
     * Creates new route with collected station points in {@code allRoutePoints} with current car and add it to {@code routes}.
     * Resets cached waybills and stations after creation.
     * @param warehouse warehouse as start point of route.
     */
    private void addNewRoute(Warehouse warehouse) {
        List<Store> allRoutePoints = waybillsByOrderMap.values().stream().map(w -> w.getStoreOrder().getStore()).distinct().collect(Collectors.toList());
        Route route = new Route(null, false,
                getNextDriver(),
                currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                warehouse,allRoutePoints,
                false);
        route.getWaybillList().forEach(w -> {
            w.setRoute(route);
            w.updateProductCost();
        });
        routes.add(route);
        currentCar.resetFullness();
        currentCar = carAssigner.getNextMostFreeCar();
        resetCache();
    }

    /**
     * Clear {@code tackedStationPoints} list and {@code waybillsByOrderMap} map.
     */
    private void resetCache() {
        waybillsByOrderMap.clear();
        tackedStationPoints.clear();
    }

    /**
     * Reset assign product quantity in {@code tackedStationPoints} list for car level increasing.
     */
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

    /**
     * Method that transform distribution entries to {@code AssignOrderLine} list.
     * @param entries entries to transform.
     * @return list of {@code AssignOrderLine} with equal product, quantity, order and zero assigning.
     */
    private List<AssignOrderLine> getOrderLinesFromEntryList(List<DistributionEntry> entries) {
        return entries.stream()
                .flatMap(e -> e.getOrderProductLines().stream())
                .map(ordLine -> new AssignOrderLine(ordLine.getProduct(), ordLine.getQuantity(),
                        ordLine.getStoreOrder(), 0))
                .collect(Collectors.toList());
    }

    /**
     * Method that group distribution entries by warehouse.
     * @param entries entries to group.
     * @return list of {@code WarehouseEntryGroup} by each warehouse.
     */
    private List<WarehouseEntryGroup> groupEntriesByWarehouse(List<DistributionEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(e -> e.getDistributionKey().getWarehouse()))
                .entrySet()
                .stream()
                .map(e -> new WarehouseEntryGroup(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Method that group distribution entries by store.
     *
     * @param entries entries to group.
     * @return map of grouped {@code DistributionEntry}
     */
    private Map<Store, List<DistributionEntry>> groupEntriesByStore(List<DistributionEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(e -> e.getDistributionKey().getStore()));
    }

    /**
     * Return next the most free driver. After pooling it increase load point by one and push driver to queue again.
     * @return the most free driver from queue.
     */
    private Driver getNextDriver() {
        DriverAssignEntry driverAssignEntry = drivers.poll();
        driverAssignEntry.incrementAssign();
        drivers.add(driverAssignEntry);
        return driverAssignEntry.getDriver();
    }
}
