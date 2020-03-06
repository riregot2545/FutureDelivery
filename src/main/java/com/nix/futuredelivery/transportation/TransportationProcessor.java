package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Driver;
import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.repository.CarRepository;
import com.nix.futuredelivery.repository.DriverRepository;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.vrpsolver.TestVehicleRouter;
import com.nix.futuredelivery.transportation.vrpsolver.VehicleRoutingSolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportationProcessor {
    private final TransportationGrouper transportationGrouper;
    private TransportationAssigner transportationAssigner;
    private VehicleRoutingSolver routingSolver;

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final RouteRepository routeRepository;
    private final StoreOrderRepository orderRepository;

    public void proceedOrders() {
        List<DistributionEntry> distributionEntries = transportationGrouper.distributeAllFreeOrders();
        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        transportationAssigner = new TransportationAssigner(cars, drivers, distributionEntries);
        List<Route> assignedRoutes = transportationAssigner.assign();

        routingSolver = new TestVehicleRouter();
        List<Route> sortedRoutes = routingSolver.setOrderInWaybills(assignedRoutes);
        routeRepository.saveAll(sortedRoutes);

        List<StoreOrder> storeOrders = distributionEntries.stream()
                .flatMap(e -> e.getOrderProductLines().stream())
                .map(OrderProductLine::getStoreOrder)
                .distinct()
                .collect(Collectors.toList());
        storeOrders.forEach(ord -> ord.setDistributed(true));

        orderRepository.saveAll(storeOrders);
    }
}
