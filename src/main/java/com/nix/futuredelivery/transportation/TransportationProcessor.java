package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.repository.*;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DriverLoad;
import com.nix.futuredelivery.transportation.model.RoadDriving;
import com.nix.futuredelivery.transportation.model.exceptions.NoneCarsExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.NoneDriversExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOverselledException;
import com.nix.futuredelivery.transportation.vrpsolver.TestVehicleRouter;
import com.nix.futuredelivery.transportation.vrpsolver.VehicleRoutingSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public List<Route> proceedOrders() throws NoneCarsExistsException, NoneDriversExistsException, ProductsIsOverselledException {
        Queue<DriverLoad> drivers = getDriversQueue();
        List<Car> cars = carRepository.findAll();

        if (cars.isEmpty())
            throw new NoneCarsExistsException();

        List<DistributionEntry> distributionEntries = transportationGrouper.distributeAllFreeOrders();
        if (distributionEntries.isEmpty()) {
            log.info("Distribution list is empty, nothing to assign, returning.");
            return new ArrayList<>();
        }
        transportationAssigner = new TransportationAssigner(cars, drivers, distributionEntries);
        List<Route> assignedRoutes = transportationAssigner.assign();

/*        List<Store> collect = assignedRoutes.get(0).getWaybillList().stream().map(w -> w.getStoreOrder().getStore()).distinct().collect(Collectors.toList());
//
            Lisct<Distace> distance list = new ...Ar
           for(Store store : collect){
            ///!!!! обновить проект, так как функции тут нет
                  distanceList.add( distanceRep.findByAddreasFrom(store.getAddress)
           }


//        Optional<Distance> oneByAddressFromAndAddressTo = distanceRepository.findOneByAddressFromAndAddressTo(collect.get(0).getAddress(), collect.get(1).getAddress());
//        oneByAddressFromAndAddressTo.get().getDistance();
//
//        Map<Store, List<Waybill>> collect1 = assignedRoutes.get(0).getWaybillList().stream().collect(Collectors.groupingBy(w -> w.getStoreOrder().getStore()));
//
//        for (Waybill waybill : assignedRoutes.get(0).getWaybillList()) {
//            waybill.setDeliveryQueuePlace();
//        }

 */

        routingSolver = new TestVehicleRouter();
        List<Route> sortedRoutes = routingSolver.setOrderInWaybills(assignedRoutes);
        List<Route> calculateRoutes = calculateRouteCosts(sortedRoutes);
        saveRoutes(calculateRoutes);
        cleanStoreOrders(distributionEntries);
        return calculateRoutes;
    }

    @Transactional
    private void saveRoutes(List<Route> routeList) {
        routeRepository.saveAll(routeList);

        for (Route route : routeList) {
            waybillRepository.saveAll(route.getWaybillList());
        }
    }

    private void cleanStoreOrders(List<DistributionEntry> distributionEntries) {
        log.info("Starting order cleaning after route creation...");
        List<StoreOrder> storeOrders = distributionEntries.stream()
                .flatMap(e -> e.getOrderProductLines().stream())
                .map(OrderProductLine::getStoreOrder)
                .distinct()
                .collect(Collectors.toList());
        storeOrders.forEach(ord -> ord.setDistributed(true));

        orderRepository.saveAll(storeOrders);

        log.info("All assigned orders marked as distributed.");
    }

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
                BigDecimal mainDeliveryCost = new BigDecimal(0);

                accumulatedDistance.setDistance(accumulatedDistance.getDistance() +
                        storeDistanceMap.get((mainWaybill.getStoreOrder().getStore())).getDistance().getDistance());


                BigDecimal mainReducedCost = mainWaybill.getProductLines().stream()
                        .map(line -> new BigDecimal(line.getProduct().getVolume().getVolume() *
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
                            .map(line -> new BigDecimal(line.getProduct().getVolume().getVolume() *
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

    private Map<AbstractStation, RoadDriving> getDistances(List<Store> stores, Warehouse warehouse) {
        Map<AbstractStation, RoadDriving> distanceMap = new HashMap<>();

        Store firstStore = stores.get(0);
        Optional<Distance> firstDistance = distanceRepository.findOneByAddressFromAndAddressTo(warehouse.getAddress(), firstStore.getAddress());
        if (firstDistance.isPresent()) {
            distanceMap.put(firstStore, new RoadDriving(warehouse, firstStore, firstDistance.get()));
        } else {
            throw new IllegalStateException("There is not any distance between points: " + warehouse + " and " + firstStore);
        }


        for (int i = 0; i < stores.size() - 1; i++) {
            Store store = stores.get(i);
            Store nextStore = stores.get(i + 1);
            Optional<Distance> distance = distanceRepository.findOneByAddressFromAndAddressTo(nextStore.getAddress(), store.getAddress());
            if (distance.isPresent()) {
                distanceMap.put(nextStore, new RoadDriving(store, nextStore, distance.get()));
            } else {
                throw new IllegalStateException("There is not any distance between points: " + store + " and " + nextStore);
            }
        }
        return distanceMap;
    }

    private List<Store> getStoresInOrderOfVisiting(Route route) {
        return route.getWaybillList().stream()
                .sorted(Comparator.comparingInt(Waybill::getDeliveryQueuePlace))
                .map(w -> w.getStoreOrder().getStore())
                .distinct()
                .collect(Collectors.toList());
    }

    private Queue<DriverLoad> getDriversQueue() throws NoneDriversExistsException {
        List<DriverLoad> driverLoads = driverRepository.aggregateDriverByLoad();
        if (driverLoads.isEmpty())
            throw new NoneDriversExistsException();
        PriorityQueue<DriverLoad> driverQueue = new PriorityQueue<>(Comparator.comparingLong(DriverLoad::getLoad));
        driverQueue.addAll(driverLoads);
        return driverQueue;
    }
}
