package com.nix.futuredelivery.distribution;

import com.nix.futuredelivery.distribution.tsolver.model.DistributionCell;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionCostMatrixBuilder;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionPlan;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductAggregator {
    private final StoreOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceRepository distanceRepository;

    @Transactional
    public void aggregate(){
        Map<Product, List<OrderProductLine>> productOrderMap = groupOrderLinesByProduct();
        Map<Product, List<WarehouseProductLine>> productWarehouseMap = groupWarehouseLinesByProduct();

        if(productWarehouseMap.keySet().equals(productOrderMap.keySet()))
        {
            List<DistributionEntry> productDistributionEntries = new ArrayList<>();
            productOrderMap.forEach((product,list)->{

                List<WarehouseProductLine> warehouseProductLines = productWarehouseMap.get(product);

                DistributionParticipants.Builder participantsBuilder =
                        new DistributionParticipants.Builder();

                for (WarehouseProductLine warehouseProductLine : warehouseProductLines) {
                    participantsBuilder.addSupplier(warehouseProductLine.getWarehouse(),warehouseProductLine.getQuantity());
                }

                Map<Store, List<OrderProductLine>> storeProductLinesMap = groupOrderLinesByStore(list);

                storeProductLinesMap.forEach((store, orderLine) ->
                        participantsBuilder.addConsumer(store, orderLine.stream().mapToInt(AbstractProductLine::getQuantity).sum())
                );

                DistributionParticipants participants = participantsBuilder.build();

                DistributionCostMatrixBuilder costMatrixBuilder = new DistributionCostMatrixBuilder(participants,distanceRepository);
                DistributionCell[][] costMatrix = costMatrixBuilder.build();

                ProductDistributor distributor = new ProductDistributor(costMatrix,participants);
                DistributionPlan distributionPlan = distributor.distribute();

                for (int i = 0; i < participants.suppliersCount(); i++) {
                    for (int j = 0; j < participants.consumersCount(); j++) {
                        if(!participants.isConsumerFictive(j)
                            &&  !distributionPlan.getCell(i, j).isFullnessEmpty()) {
                           productDistributionEntries.add(new DistributionEntry(
                                   participants.getConsumerStore(j),
                                   participants.getSupplierWarehouse(i),
                                   product,
                                   storeProductLinesMap.get(participants.getConsumerStore(j))
                           ));
                        }
                    }
                }


            });
        }
        else
        {
            throw new IllegalStateException("Product positions in order and warehouse does not match.");
        }

    }

    private int getStoresCount(List<StoreOrder> orders){
        return (int) orders.stream().map(o->o.getStore().getId()).distinct().count();
    }

    private Map<Product, List<OrderProductLine>> groupOrderLinesByProduct(){
        Map<Product, List<OrderProductLine>> productOrderMap = new HashMap<>();
        List<StoreOrder> orders = orderRepository.findByIsDistributedFalse();
        for (StoreOrder order : orders) {
            for (OrderProductLine productLine : order.getProductLines()) {
                productOrderMap.computeIfAbsent(productLine.getProduct(), k -> new ArrayList<>());
                productOrderMap.get(productLine.getProduct()).add(productLine);
            }
        }
        return productOrderMap;
    }

    private Map<Product, List<WarehouseProductLine>> groupWarehouseLinesByProduct(){
        Map<Product, List<WarehouseProductLine>> productWarehouseMap = new HashMap<>();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        for (Warehouse warehouse : warehouses) {
            for (WarehouseProductLine productLine : warehouse.getProductLines()) {
                productWarehouseMap.computeIfAbsent(productLine.getProduct(), k -> new ArrayList<>());
                productWarehouseMap.get(productLine.getProduct()).add(productLine);
            }
        }

        return productWarehouseMap;
    }

    private Map<Store, List<OrderProductLine>> groupOrderLinesByStore(List<OrderProductLine> lines) {
        return lines.stream().collect(Collectors.groupingBy(e -> e.getStoreOrder().getStore()));
    }

    private List<StoreOrder> getOrdersByStore(List<OrderProductLine> list, Store store) {
        return list.stream()
                .filter(line -> line.getStoreOrder().getStore().equals(store))
                .map(OrderProductLine::getStoreOrder).collect(Collectors.toList());
    }
}
