package com.nix.futuredelivery.distribution;

import com.nix.futuredelivery.distribution.tsolver.model.DistributionCell;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionCostMatrixBuilder;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.distribution.tsolver.model.DistributionPlan;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Warehouse;
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

                Map<Store, OrderProductLine> storeProductLinesMap = groupOrderLinesByStore(list);

                storeProductLinesMap.forEach((store, orderLine)-> participantsBuilder.addConsumer(store,orderLine.getQuantity()));

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
                                   distributionPlan.getCell(i,j).getFullness()
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

    private Map<Store, OrderProductLine> groupOrderLinesByStore(List<OrderProductLine> lines){
        Map<Store, OrderProductLine> storeProductLinesMap = new HashMap<>();

        for (OrderProductLine orderProductLine : lines) {
            if(!storeProductLinesMap.containsKey(orderProductLine.getStoreOrder().getStore()))
                storeProductLinesMap.put(orderProductLine.getStoreOrder().getStore(),orderProductLine);
            else {
                OrderProductLine orderProductLine1 = storeProductLinesMap.get(orderProductLine.getStoreOrder().getStore());
                orderProductLine1.setQuantity(orderProductLine1.getQuantity() + orderProductLine.getQuantity());
            }
        }
        return storeProductLinesMap;
    }
}
