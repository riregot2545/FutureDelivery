package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.*;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;

import java.util.*;
import java.util.stream.Collectors;

public class PolarDistributionSolver {
    public PolarDistributionSolver(List<Car> cars, Map<Warehouse, List<DistributionEntry>> mappedWarehouses, List<Driver> drivers) {
        this.carAssigner = new CarAssigner(cars);
        this.mappedWarehouses = mappedWarehouses;
        this.drivers = new LinkedList<>(drivers);
    }

    private final CarAssigner carAssigner;
    private final Queue<Driver> drivers;
    private final Map<Warehouse, List<DistributionEntry>> mappedWarehouses;


    public void assign() {
        List<Route> routes = new ArrayList<>();


        mappedWarehouses.forEach((warehouse, entries) -> {
            List<StationPoint> stationPoints = new ArrayList<>();

            Map<Store, List<DistributionEntry>> productEntriesGroupedByStore = groupEntriesByStore(entries);


            productEntriesGroupedByStore.forEach((store, groupedEntries) -> {
                List<OrderProductLine> collectedOrderLines = groupedEntries.stream()
                        .flatMap(e -> e.getOrderProductLines().stream())
                        .collect(Collectors.toList());
                StationPoint point = new StationPoint(
                        getAngleBetweenLocations(warehouse.getAddress().getPointLocation(), store.getAddress().getPointLocation()),
                        store,
                        warehouse,
                        collectedOrderLines.stream()
                                .map(ordLine -> new AssignOrderLine(ordLine.getProduct(), ordLine.getQuantity(),
                                        ordLine.getStoreOrder(), 0)).collect(Collectors.toList()),
                        new Volume(collectedOrderLines.stream().mapToDouble(e -> e.getProduct().getVolume().getVolume() * e.getQuantity()).sum()),
                        Volume.empty()
                );
                stationPoints.add(point);
            });

            stationPoints.sort(Comparator.comparingDouble(StationPoint::getAngle));

            Car currentCar = carAssigner.getNextMostFreeCar();

            List<StationPoint> tackedStationPoints = new ArrayList<>();
            Map<StoreOrder, Waybill> waybillsByOrderMap = new HashMap<>();
            for (int i = 0; i < stationPoints.size(); i++) {
                StationPoint currentPoint = stationPoints.get(i);

                for (AssignOrderLine productLine : currentPoint.getProductLines()) {
                    if (productLine.getRemainQuantity() > 0) {
                        StoreOrder order = productLine.getStoreOrder();

                        double oneProductVolume = productLine.getProduct().getVolume().getVolume();
                        if (currentCar.getFreeVolume() >= oneProductVolume) {
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
                        } else {
                            if (tackedStationPoints.size() < 2) {
                                Optional<Capacity> groupIncrementResult = carAssigner.incrementGroupLevel();
                                if (groupIncrementResult.isPresent()) {
                                    carAssigner.resetAssignCar(currentCar);
                                    currentCar = carAssigner.getNextMostFreeCar(false);
                                    i = -1;
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
                                    waybillsByOrderMap.clear();
                                    tackedStationPoints.clear();
                                    break;
                                } else {
                                    routes.add(new Route(null,
                                            getNextDriver(),
                                            currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                                            warehouse));
                                    currentCar.resetFullness();
                                    currentCar = carAssigner.getNextMostFreeCar();
                                    i = -1;
                                    waybillsByOrderMap.clear();
                                    tackedStationPoints.clear();
                                    break;
                                }

                            } else {
                                routes.add(new Route(null,
                                        getNextDriver(),
                                        currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                                        warehouse));
                                currentCar.resetFullness();
                                currentCar = carAssigner.getNextMostFreeCar();
                                i = -1;
                                waybillsByOrderMap.clear();
                                tackedStationPoints.clear();
                                break;
                            }
                        }
                    }
                }

                if ((i + 1) >= stationPoints.size() && stationHasDemand(currentPoint))
                    i -= 2;
            }

            routes.add(new Route(null,
                    getNextDriver(),
                    currentCar, new ArrayList<>(waybillsByOrderMap.values()),
                    warehouse));


        });
    }

    private boolean stationHasDemand(StationPoint point) {
        return point.getProductLines().stream().anyMatch(line -> line.getRemainQuantity() > 0);
    }

    private boolean isVolumeCanPlaceAnyProduct(List<StationPoint> stationPoints, Volume volume) {
        return stationPoints.stream()
                .flatMap(st -> st.getProductLines().stream())
                .filter(st -> st.getRemainQuantity() > 0)
                .map(AbstractProductLine::getProduct)
                .anyMatch(p -> p.getVolume().getVolume() <= volume.getVolume());
    }

    private Driver getNextDriver() {
        return drivers.poll();
    }

    private Map<Store, List<DistributionEntry>> groupEntriesByStore(List<DistributionEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(e -> e.getDistributionKey().getStore()));
    }

    private double getAngleBetweenLocations(Location center, Location point) {

        double differLong = Math.toRadians(point.getLongitude() - center.getLongitude());

        double centerLatRadians = Math.toRadians(center.getLatitude());
        double pointLatRadians = Math.toRadians(point.getLatitude());

        double y = Math.sin(differLong) * Math.cos(pointLatRadians);
        double x = Math.cos(centerLatRadians) * Math.sin(pointLatRadians) -
                Math.sin(centerLatRadians) * Math.cos(pointLatRadians) * Math.cos(differLong);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }
}
