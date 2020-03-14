package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DistributionEntry.DistributionKey;
import com.nix.futuredelivery.transportation.model.ProductKeyListGroup;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOverselledException;
import com.nix.futuredelivery.transportation.tsolver.ProductDistributor;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionCell;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionCostMatrixBuilder;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionPlan;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TransportationGrouper {
    private final StoreOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceRepository distanceRepository;

    private final Map<DistributionKey, DistributionEntry> productDistributionEntries;

    public List<DistributionEntry> distributeAllFreeOrders() throws ProductsIsOverselledException {
        List<ProductKeyListGroup<OrderProductLine>> orderGroupCatalog = groupOrderLinesByProduct();
        List<ProductKeyListGroup<WarehouseProductLine>> warehouseGroupCatalog = groupWarehouseLinesByProduct();
        Map<Warehouse, List<Distance>> cachedDistances = cacheDistances(warehouseGroupCatalog);

        if (orderGroupCatalog.isEmpty()) {
            log.info("There is not any undistributed orders in database");
            return new ArrayList<>();
        }
        Optional<ProductsIsOverselledException> isOversells = isProductQuantityEnough(orderGroupCatalog, warehouseGroupCatalog);
        if (isOversells.isPresent())
            throw isOversells.get();

        if (isProductPositionsEquals(orderGroupCatalog, warehouseGroupCatalog)) {
            log.info("Starting product distribution...");
            for (int i = 0; i < orderGroupCatalog.size(); i++) {
                log.info("Product distribution {}/{}", i, orderGroupCatalog.size());
                ProductKeyListGroup<OrderProductLine> orderProductGroup = orderGroupCatalog.get(i);
                Product currentProduct = orderProductGroup.getKey();
                ProductKeyListGroup<WarehouseProductLine> warehouseProductGroup = getProductGroupByKey(warehouseGroupCatalog, currentProduct).get();

                Map<Store, List<AssignOrderLine>> storeProductLinesMap = groupOrderLinesByStore(orderProductGroup.getList());
                DistributionParticipants participants = makeDistributionParticipants(warehouseProductGroup, storeProductLinesMap);

                DistributionCostMatrixBuilder costMatrixBuilder = new DistributionCostMatrixBuilder(participants, cachedDistances);
                DistributionCell[][] costMatrix = costMatrixBuilder.build();

                ProductDistributor distributor = new ProductDistributor(costMatrix, participants);
                DistributionPlan distributionPlan = distributor.distribute();

                decodeDistributionPlan(distributionPlan, currentProduct, storeProductLinesMap);

            }
            log.info("Product distribution done.");
            return new ArrayList<>(productDistributionEntries.values());
        } else {
            throw new IllegalStateException("Product positions in order and warehouse does not match.");
        }
    }

    private void decodeDistributionPlan(DistributionPlan distributionPlan, Product currentProduct,
                                        Map<Store, List<AssignOrderLine>> storeProductLinesMap) {
        DistributionParticipants participants = distributionPlan.getParticipants();
        for (int i = 0; i < distributionPlan.getHeight(); i++) {
            for (int j = 0; j < distributionPlan.getWidth(); j++) {
                DistributionCell cell = distributionPlan.getCell(i, j);

                if (!participants.isConsumerFictive(j) && !cell.isFullnessEmpty()) {
                    DistributionKey key = new DistributionKey(participants.getConsumerStore(j), currentProduct,
                            participants.getSupplierWarehouse(i));

                    List<OrderProductLine> filledProductLines = new ArrayList<>();
                    List<AssignOrderLine> assignOrderLines = storeProductLinesMap.get(participants.getConsumerStore(j));
                    int remainFullness = cell.getFullness();
                    for (AssignOrderLine orderProductLine : assignOrderLines) {
                        if (remainFullness > 0) {
                            if (orderProductLine.getRemainQuantity() >= remainFullness) {
                                filledProductLines.add(new OrderProductLine(orderProductLine.getProduct(), remainFullness, orderProductLine.getStoreOrder()));
                                orderProductLine.addAssignQuantity(remainFullness);
                                remainFullness = 0;
                                break;
                            } else {
                                filledProductLines.add(new OrderProductLine(orderProductLine.getProduct(), orderProductLine.getRemainQuantity(), orderProductLine.getStoreOrder()));
                                orderProductLine.addAssignQuantity(orderProductLine.getRemainQuantity());
                                remainFullness -= orderProductLine.getRemainQuantity();
                            }
                        }
                    }
                    if (productDistributionEntries.get(key) == null)
                        productDistributionEntries.put(key, new DistributionEntry(key, filledProductLines));
                    else {
                        productDistributionEntries.get(key).getOrderProductLines().addAll(filledProductLines);
                    }
                }

            }
        }
    }

    private DistributionParticipants makeDistributionParticipants(ProductKeyListGroup<WarehouseProductLine> warehouseProductGroup,
                                                                  Map<Store, List<AssignOrderLine>> storeProductLinesMap) {
        DistributionParticipants.Builder participantsBuilder =
                new DistributionParticipants.Builder();

        warehouseProductGroup.getList().forEach(warehouseProductLine ->
                participantsBuilder.addSupplier(warehouseProductLine.getWarehouse(), warehouseProductLine.getQuantity()));

        storeProductLinesMap.forEach((store, orderLine) ->
                participantsBuilder.addConsumer(store, orderLine.stream().mapToInt(AbstractProductLine::getQuantity).sum())
        );

        return participantsBuilder.build();
    }

    private boolean isProductPositionsEquals(List<ProductKeyListGroup<OrderProductLine>> orderGroupList,
                                             List<ProductKeyListGroup<WarehouseProductLine>> warehouseGroupList) {
        Set<Product> ordSet = orderGroupList.stream().map(ProductKeyListGroup::getKey).collect(Collectors.toSet());
        Set<Product> warSet = warehouseGroupList.stream().map(ProductKeyListGroup::getKey).collect(Collectors.toSet());
        return warSet.containsAll(ordSet);
    }

    private Optional<ProductsIsOverselledException> isProductQuantityEnough(List<ProductKeyListGroup<OrderProductLine>> orderGroupList,
                                                                            List<ProductKeyListGroup<WarehouseProductLine>> warehouseGroupList) {
        for (ProductKeyListGroup<OrderProductLine> productGroup : orderGroupList) {
            Product key = productGroup.getKey();
            ProductKeyListGroup<WarehouseProductLine> warehouseGroup = warehouseGroupList.stream().filter(g -> g.getKey().equals(key)).findFirst().get();
            int productSum = productGroup.getList().stream().mapToInt(AbstractProductLine::getQuantity).sum();
            int warehouseStock = warehouseGroup.getList().stream().mapToInt(AbstractProductLine::getQuantity).sum();
            if (productSum > warehouseStock)
                return Optional.of(new ProductsIsOverselledException(productSum, warehouseStock));
        }
        return Optional.empty();
    }

    private <T extends AbstractProductLine> Optional<ProductKeyListGroup<T>> getProductGroupByKey(List<ProductKeyListGroup<T>> groupList,
                                                                                                  Product key) {
        return groupList.stream().filter(group -> group.getKey().equals(key)).findFirst();
    }

    private List<ProductKeyListGroup<OrderProductLine>> groupOrderLinesByProduct() {
        List<StoreOrder> orders = orderRepository.findByIsDistributedFalse();

        return orders.stream()
                .flatMap(ord -> ord.getProductLines().stream())
                .collect(Collectors.groupingBy(AbstractProductLine::getProduct))
                .entrySet()
                .stream()
                .map(entry -> new ProductKeyListGroup<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<ProductKeyListGroup<WarehouseProductLine>> groupWarehouseLinesByProduct() {
        List<Warehouse> warehouses = warehouseRepository.findAll();

        return warehouses.stream()
                .flatMap(ord -> ord.getProductLines().stream())
                .collect(Collectors.groupingBy(AbstractProductLine::getProduct))
                .entrySet()
                .stream()
                .map(entry -> new ProductKeyListGroup<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<Store, List<AssignOrderLine>> groupOrderLinesByStore(List<OrderProductLine> lines) {
        List<AssignOrderLine> collect = lines.stream()
                .map(line -> new AssignOrderLine(line.getProduct(), line.getQuantity(), line.getStoreOrder(), 0)).collect(Collectors.toList());
        return collect.stream().collect(Collectors.groupingBy(e -> e.getStoreOrder().getStore()));

    }

    private Map<Warehouse, List<Distance>> cacheDistances(List<ProductKeyListGroup<WarehouseProductLine>> warehouseGroupCatalog) {
        List<Warehouse> warehouses = warehouseGroupCatalog.stream()
                .flatMap(g -> g.getList().stream())
                .map(WarehouseProductLine::getWarehouse)
                .distinct()
                .collect(Collectors.toList());
        Map<Warehouse, List<Distance>> mapOfDistances = new HashMap<>();
        for (Warehouse warehouse : warehouses) {
            List<Distance> byWarehouse = distanceRepository.findByAddressFrom(warehouse.getAddress());
            mapOfDistances.put(warehouse, byWarehouse);
        }

        return mapOfDistances;
    }
}
