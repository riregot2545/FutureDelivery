package com.nix.futuredelivery.transportation;

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
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DistributionEntry.DistributionKey;
import com.nix.futuredelivery.transportation.model.ProductKeyListGroup;
import com.nix.futuredelivery.transportation.tsolver.ProductDistributor;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionCell;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionCostMatrixBuilder;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionParticipants;
import com.nix.futuredelivery.transportation.tsolver.model.DistributionPlan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransportationGrouper {
    private final StoreOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceRepository distanceRepository;

    private final Map<DistributionKey, DistributionEntry> productDistributionEntries;

    @Transactional
    public List<DistributionEntry> distributeAllFreeOrders() {
        List<ProductKeyListGroup<OrderProductLine>> orderGroupCatalog = groupOrderLinesByProduct();
        List<ProductKeyListGroup<WarehouseProductLine>> warehouseGroupCatalog = groupWarehouseLinesByProduct();

        if (isProductPositionsEquals(orderGroupCatalog, warehouseGroupCatalog)) {
            for (ProductKeyListGroup<OrderProductLine> orderProductGroup : orderGroupCatalog) {
                Product currentProduct = orderProductGroup.getKey();
                ProductKeyListGroup<WarehouseProductLine> warehouseProductGroup = getProductGroupByKey(warehouseGroupCatalog, currentProduct).get();

                Map<Store, List<AssignOrderLine>> storeProductLinesMap = groupOrderLinesByStore(orderProductGroup.getList());
                DistributionParticipants participants = makeDistributionParticipants(orderProductGroup, warehouseProductGroup, storeProductLinesMap);

                DistributionCostMatrixBuilder costMatrixBuilder = new DistributionCostMatrixBuilder(participants, distanceRepository);
                DistributionCell[][] costMatrix = costMatrixBuilder.build();

                ProductDistributor distributor = new ProductDistributor(costMatrix, participants);
                DistributionPlan distributionPlan = distributor.distribute();

                decodeDistributionPlan(distributionPlan, currentProduct, storeProductLinesMap);

            }
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

    private DistributionParticipants makeDistributionParticipants(ProductKeyListGroup<OrderProductLine> orderProductGroup,
                                                                  ProductKeyListGroup<WarehouseProductLine> warehouseProductGroup,
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
        Set<Product> set1 = orderGroupList.stream().map(ProductKeyListGroup::getKey).collect(Collectors.toSet());
        Set<Product> set2 = warehouseGroupList.stream().map(ProductKeyListGroup::getKey).collect(Collectors.toSet());

        return set1.equals(set2);
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
        return lines.stream()
                .map(line -> new AssignOrderLine(line.getProduct(), line.getQuantity(), line.getStoreOrder(), 0))
                .collect(Collectors.groupingBy(e -> e.getStoreOrder().getStore()));
    }
}
