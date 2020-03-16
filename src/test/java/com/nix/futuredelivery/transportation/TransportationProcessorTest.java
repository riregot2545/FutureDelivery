package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Waybill;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.repository.*;
import com.nix.futuredelivery.transportation.model.exceptions.NoneCarsExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.NoneDriversExistsException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOverselledException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransportationProcessorTest {
    @Autowired
    private TransportationProcessor transportationProcessor;

    @Autowired
    private DistanceRepository distanceRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private StoreOrderRepository orderRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private WaybillRepository waybillRepository;

    @Test
    @Transactional
    void proceedOrders() throws NoneCarsExistsException, NoneDriversExistsException, ProductsIsOverselledException {
        List<StoreOrder> orders = orderRepository.findByisDistributedFalse();
        List<Route> routes = transportationProcessor.proceedOrders();

        int expectedQuantitySum = orders.stream().flatMap(ord -> ord.getProductLines().stream()).mapToInt(AbstractProductLine::getQuantity).sum();
        int realQuantitySum = routes.stream().flatMap(r -> r.getWaybillList().stream()).flatMap(w -> w.getProductLines().stream()).mapToInt(AbstractProductLine::getQuantity).sum();

        assertEquals(expectedQuantitySum, realQuantitySum);


        double expectedVolumeSum = orders.stream().flatMap(ord -> ord.getProductLines().stream()).mapToDouble(line -> line.getQuantity() * line.getProduct().getVolume().getVolume()).sum();
        double realVolumeSum = routes.stream().flatMap(r -> r.getWaybillList().stream()).flatMap(w -> w.getProductLines().stream()).mapToDouble(line -> line.getQuantity() * line.getProduct().getVolume().getVolume()).sum();

        assertEquals(expectedVolumeSum, realVolumeSum);

        for (StoreOrder order : orders) {
            List<Waybill> waybillsByOrder = routes.stream().flatMap(r -> r.getWaybillList().stream()).filter(w -> w.getStoreOrder().equals(order)).collect(Collectors.toList());

            for (OrderProductLine productLine : order.getProductLines()) {
                int realQuantity = waybillsByOrder.stream().flatMap(w -> w.getProductLines().stream()).filter(line -> line.getProduct().equals(productLine.getProduct())).mapToInt(AbstractProductLine::getQuantity).sum();
                assertEquals(productLine.getQuantity(), realQuantity);
            }
        }

    }
}