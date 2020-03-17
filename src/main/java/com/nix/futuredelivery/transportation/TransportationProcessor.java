package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.OrderStatus;
import com.nix.futuredelivery.repository.*;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DriverAssignEntry;
import com.nix.futuredelivery.transportation.model.RoadDriving;
import com.nix.futuredelivery.transportation.model.exceptions.NoneCarsExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.NoneDriversExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductPositionNotExistException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOversellsException;
import com.nix.futuredelivery.transportation.vrpsolver.SimulatedVehicleRouter;
import com.nix.futuredelivery.transportation.vrpsolver.VehicleRoutingSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class which controls all transportation process
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransportationProcessor {
    private final TransportationGrouper transportationGrouper;
    private TransportationAssigner transportationAssigner;
    private VehicleRoutingSolver routingSolver;

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final RouteRepository routeRepository;
    private final StoreOrderRepository orderRepository;
    private final DistanceRepository distanceRepository;
    private final WaybillRepository waybillRepository;

    /**
     * Cached distances for vrp and cost calculations
     */
    private List<Distance> distances;


    /**
     * Transactional method to begin transportation calculation process.
     * First of all system fill driver and car queue for future assigning.
     * Then {@code TransportationGrouper} aggregate all undistributed orders, build
     * and resolve distribution plans. After that {@code TransportationAssigner} assign
     * cars and drivers. Finally {@code VehicleRoutingSolver} build most optimized delivery order.
     * At the end orders change status to distributed.
     *
     * @return a list that contain all built routes or empty list if nothing to assign.
     * @throws NoneCarsExistsException      if car repository is empty.
     * @throws NoneDriversExistsException   if driver repository is empty.
     * @throws ProductsIsOversellsException if product quantity in orders bigger than available stock on warehouse.
     * @throws ProductPositionNotExistException if product positions in order group does not compliance warehouse stock.
     */
    @Transactional
    public List<Route> proceedOrders() throws NoneCarsExistsException, NoneDriversExistsException, ProductsIsOversellsException, ProductPositionNotExistException {
        Queue<DriverAssignEntry> drivers = getDriversQueue();
        List<Car> cars = carRepository.findAll();

        if (cars.isEmpty())
            throw new NoneCarsExistsException();

        List<DistributionEntry> distributionEntries = transportationGrouper.distributeAllNewOrders();
        if (distributionEntries.isEmpty()) {
            log.info("Distribution list is empty, nothing to assign, returning.");
            return new ArrayList<>();
        }
        transportationAssigner = new TransportationAssigner(cars, drivers, distributionEntries);
        List<Route> assignedRoutes = transportationAssigner.assign();


        routingSolver = new SimulatedVehicleRouter();
        distances = cacheDistances(assignedRoutes);
        List<Route> sortedRoutes = routingSolver.setOrderInWaybills(assignedRoutes, distances);
        List<Route> calculateRoutes = calculateRouteCosts(sortedRoutes);
        saveRoutes(calculateRoutes);
        updateStoreOrders(distributionEntries);
        return calculateRoutes;
    }

    /**
     * Transactional method that saves routes to repository
     * @param routeList that contains routes to save
     */
    @Transactional
    private void saveRoutes(List<Route> routeList) {
        routeRepository.saveAll(routeList);

        for (Route route : routeList) {
            waybillRepository.saveAll(route.getWaybillList());
        }
    }

    /**
     * Method that cache all used distances in route in order to
     * speed up vrp and consumption calculation process
     * @param routeList that contains routes with route points
     * @return list of cached distances
     */
    private List<Distance> cacheDistances(List<Route> routeList) {
        List<Store> collect = routeList.stream()
                .flatMap(r -> r.getWaybillList().stream())
                .map(w -> w.getStoreOrder().getStore())
                .distinct()
                .collect(Collectors.toList());

        ArrayList<Distance> cachedDistances = new ArrayList<>();
        for (Store store : collect) {
            cachedDistances.addAll(distanceRepository.findByAddressFrom(store.getAddress()));
        }

        List<Warehouse> warehouses = routeList.stream().map(Route::getWarehouse).collect(Collectors.toList());
        for (Warehouse warehouse : warehouses) {
            cachedDistances.addAll(distanceRepository.findByAddressFrom(warehouse.getAddress()));
        }
        return cachedDistances;
    }

    /**
     * Method that updates all affected orders status to distributed
     *
     * @param distributionEntries entries used in route building
     */
    private void updateStoreOrders(List<DistributionEntry> distributionEntries) {
        log.info("Starting order cleaning after route creation...");
        List<StoreOrder> storeOrders = distributionEntries.stream()
                .flatMap(e -> e.getOrderProductLines().stream())
                .map(OrderProductLine::getStoreOrder)
                .distinct()
                .collect(Collectors.toList());
        storeOrders.forEach(ord -> ord.setOrderStatus(OrderStatus.DISTRIBUTED));

        orderRepository.saveAll(storeOrders);

        log.info("All assigned orders marked as distributed.");
    }

    /**
     * Method that fill {@code Waybill.deliveryCost} field. Delivery price consists of
     * main car consumption and relative that depends of product volume and count.
     * Since one delivery point may have many waybills, algorithm chose one, that will be main and
     * that will be contain costs for main car consumption.
     * @param routes with waybills to delivery price calculation
     * @return list of routes with calculated delivery cost in all waybills
     */
    private List<Route> calculateRouteCosts(List<Route> routes) {
        log.info("Starting route cost calculation...");
        for (Route route : routes) {
            Car car = route.getCar();
            BigDecimal carBaseSubdivided = new BigDecimal(car.getConsumption().getBaseConsumption() /
                    route.getWaybillList().stream().map(w -> w.getStoreOrder().getStore()).distinct().count());

            Map<Store, List<Waybill>> waybillGroupedByStore = route.getWaybillList()
                    .stream()
                    .collect(Collectors.groupingBy(w -> w.getStoreOrder().getStore()));

            List<Store> storesInOrderOfVisiting = getStoresInOrderOfVisiting(route);

            Map<AbstractStation, RoadDriving> storeDistanceMap = getDistances(storesInOrderOfVisiting, route.getWarehouse());

            Distance accumulatedDistance = new Distance(null, null, 0);

            for (List<Waybill> waybills : waybillGroupedByStore.values()) {
                Waybill mainWaybill = waybills.get(0);
                mainWaybill.setStoreMain(true);
                BigDecimal mainDeliveryCost = new BigDecimal(0);

                accumulatedDistance.setDistance(accumulatedDistance.getDistance() +
                        storeDistanceMap.get((mainWaybill.getStoreOrder().getStore())).getDistance().getDistance());


                BigDecimal mainReducedCost = mainWaybill.getProductLines().stream()
                        .map(line -> new BigDecimal(line.getProduct().getVolume().getVolumeWeight() *
                                line.getQuantity() *
                                car.getConsumption().getRelativeConsumption() *
                                accumulatedDistance.getDistance()))
                        .reduce(mainDeliveryCost, BigDecimal::add);

                mainDeliveryCost = mainDeliveryCost.add(carBaseSubdivided);
                mainDeliveryCost = mainDeliveryCost.add(mainReducedCost);

                mainWaybill.setDeliveryCost(mainDeliveryCost);

                for (int i = 1; i < waybills.size(); i++) {
                    Waybill waybill = waybills.get(i);
                    BigDecimal deliveryCost = new BigDecimal(0);

                    BigDecimal reducedCost = waybill.getProductLines().stream()
                            .map(line -> new BigDecimal(line.getProduct().getVolume().getVolumeWeight() *
                                    line.getQuantity() *
                                    car.getConsumption().getRelativeConsumption() *
                                    accumulatedDistance.getDistance()))
                            .reduce(deliveryCost, BigDecimal::add);
                    deliveryCost = deliveryCost.add(reducedCost);

                    waybill.setDeliveryCost(deliveryCost);
                }
            }
        }
        log.info("Successfully calculated routes {}", routes.size());
        return routes;
    }

    /**
     * Method that map distances between route points for optimal price calculation
     * @param stores store transportation point list of route
     * @param warehouse main point, start of driving way
     * @return map of transportation points connected with {@code RoadDriving} that contains 2 points and distance between them
     */
    private Map<AbstractStation, RoadDriving> getDistances(List<Store> stores, Warehouse warehouse) {
        Map<AbstractStation, RoadDriving> distanceMap = new HashMap<>();

        Store firstStore = stores.get(0);
        Optional<Distance> firstDistance = distances.stream().filter(d -> d.getAddressFrom().equals(warehouse.getAddress()) && d.getAddressTo().equals(firstStore.getAddress()) ||
                d.getAddressFrom().equals(firstStore.getAddress()) && d.getAddressTo().equals(warehouse.getAddress())).findFirst();
        if (firstDistance.isPresent()) {
            distanceMap.put(firstStore, new RoadDriving(warehouse, firstStore, firstDistance.get()));
        } else {
            throw new IllegalStateException("There is not any distance between points: " + warehouse + " and " + firstStore);
        }

        for (int i = 0; i < stores.size() - 1; i++) {
            Store store = stores.get(i);
            Store nextStore = stores.get(i + 1);
            Optional<Distance> distance = distances.stream().filter(d -> d.getAddressFrom().equals(store.getAddress()) && d.getAddressTo().equals(nextStore.getAddress()) ||
                    d.getAddressFrom().equals(nextStore.getAddress()) && d.getAddressTo().equals(store.getAddress())).findFirst();
            if (distance.isPresent()) {
                distanceMap.put(nextStore, new RoadDriving(store, nextStore, distance.get()));
            } else {
                throw new IllegalStateException("There is not any distance between points: " + store + " and " + nextStore);
            }
        }
        return distanceMap;
    }

    /**
     * Method that return stores from all route waybills
     * @param route source of waybills
     * @return list of stores in order of delivery
     */
    private List<Store> getStoresInOrderOfVisiting(Route route) {
        return route.getWaybillList().stream()
                .sorted(Comparator.comparingInt(Waybill::getDeliveryQueuePlace))
                .map(w -> w.getStoreOrder().getStore())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Method that aggregate all drivers and build priority queue depend on driver load
     * @return priority queue of {@code DriverAssignEntry} structure that contains count of assigns of any driver
     * @throws NoneDriversExistsException if driver repository is empty.
     */
    private Queue<DriverAssignEntry> getDriversQueue() throws NoneDriversExistsException {
        List<DriverAssignEntry> driverAssignEntries = driverRepository.aggregateDriverByLoad();
        if (driverAssignEntries.isEmpty())
            throw new NoneDriversExistsException();
        PriorityQueue<DriverAssignEntry> driverQueue = new PriorityQueue<>(Comparator.comparingLong(DriverAssignEntry::getAssignCount));
        driverQueue.addAll(driverAssignEntries);
        return driverQueue;
    }
}
