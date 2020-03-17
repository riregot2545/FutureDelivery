package com.nix.futuredelivery.transportation;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.OrderStatus;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.DistanceRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import com.nix.futuredelivery.transportation.model.AssignOrderLine;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import com.nix.futuredelivery.transportation.model.DistributionEntry.DistributionKey;
import com.nix.futuredelivery.transportation.model.ProductLineGroup;
import com.nix.futuredelivery.transportation.model.exceptions.PotentialConflictException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductPositionNotExistException;
import com.nix.futuredelivery.transportation.model.exceptions.ProductsIsOversellsException;
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

/**
 * Service class which group undistributed orders into distribution entries {@code DistributionEntry}
 */
@Service
@AllArgsConstructor
@Slf4j
public class TransportationGrouper {
    private final StoreOrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceRepository distanceRepository;

    private final Map<DistributionKey, DistributionEntry> productDistributionEntries;

    /**
     * Entry point of distribution process. First of all we group product lines in orders and warehouses
     * by product. Then we build distribution plans for every product using transportation solver {@code ProductDistributor}.
     * Its method {@code distribute} return optimal distribution plan. Then we decode plan and get distribution entries.
     *
     * @return list of distribution entries that contain transportation points and one product line or empty list of there is not any
     * undistributed orders in the order repository.
     * @throws ProductsIsOversellsException     if product quantity in orders bigger than available stock on warehouse.
     * @throws ProductPositionNotExistException if product positions in order group does not compliance warehouse stock.
     */
    public List<DistributionEntry> distributeAllNewOrders() throws ProductsIsOversellsException, ProductPositionNotExistException {
        List<ProductLineGroup<OrderProductLine>> orderGroupCatalog = groupOrderLinesByProduct();
        List<ProductLineGroup<WarehouseProductLine>> warehouseGroupCatalog = groupWarehouseLinesByProduct();
        Map<Warehouse, List<Distance>> cachedDistances = cacheDistances(warehouseGroupCatalog);

        if (orderGroupCatalog.isEmpty()) {
            log.info("There is not any undistributed orders in database");
            return new ArrayList<>();
        }
        Optional<ProductsIsOversellsException> isOversells = isProductQuantityEnough(orderGroupCatalog, warehouseGroupCatalog);
        if (isOversells.isPresent())
            throw isOversells.get();

        if (isProductPositionsEquals(orderGroupCatalog, warehouseGroupCatalog)) {
            log.info("Starting product distribution...");
            Collections.shuffle(orderGroupCatalog);
            for (int i = 0; i < orderGroupCatalog.size(); i++) {
                log.info("Product distribution {}/{}", i, orderGroupCatalog.size());
                ProductLineGroup<OrderProductLine> orderProductGroup = orderGroupCatalog.get(i);
                Product currentProduct = orderProductGroup.getKey();
                ProductLineGroup<WarehouseProductLine> warehouseProductGroup = getProductGroupByKey(warehouseGroupCatalog, currentProduct).get();

                Map<Store, List<AssignOrderLine>> storeProductLinesMap = groupOrderLinesByStore(orderProductGroup.getList());
                DistributionParticipants participants = makeDistributionParticipants(warehouseProductGroup, storeProductLinesMap);

                DistributionCostMatrixBuilder costMatrixBuilder = new DistributionCostMatrixBuilder(participants, cachedDistances);
                DistributionCell[][] costMatrix = costMatrixBuilder.build();

                ProductDistributor distributor = new ProductDistributor(costMatrix, participants);
                DistributionPlan distributionPlan;
                try {
                    distributionPlan = distributor.distribute();
                } catch (PotentialConflictException e) {
                    log.warn("Unresolved potential conflict, making shuffle.");
                    Collections.shuffle(orderGroupCatalog);
                    productDistributionEntries.clear();
                    i = -1;
                    continue;
                }

                decodeDistributionPlan(distributionPlan, currentProduct, storeProductLinesMap);

            }
            log.info("Product distribution done.");
            return new ArrayList<>(productDistributionEntries.values());
        } else {
            throw new ProductPositionNotExistException();
        }
    }

    /**
     * Decoding {@code DistributionPlan} means that algorithm retrieves needed product quantity for each store and linking
     * it with warehouse. As a result it fill {@code productDistributionEntries} map.
     *
     * @param distributionPlan     not empty plan to decode.
     * @param currentProduct       product that distributing now.
     * @param storeProductLinesMap map that contains order lines to fill.
     */
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

    /**
     * Method that converts group representation of transportation participants - {@code Store} and {@code Warehouse}
     * to distribution representation - {@code Consumer} and {@code Supplier} using {@code DistributionParticipants.Builder}.
     *
     * @param warehouseProductGroup group of warehouses that contain distribution product in stock.
     * @param storeProductLinesMap  map of the store order lines that contain only one distribution product.
     * @return new instance of {@code DistributionParticipants} prepared for specific product.
     */
    private DistributionParticipants makeDistributionParticipants(ProductLineGroup<WarehouseProductLine> warehouseProductGroup,
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

    /**
     * Internal check for compliance of product positions in the order and warehouse stock.
     *
     * @param orderGroupList     groups that represent all needed products.
     * @param warehouseGroupList groups that represent all available products.
     * @return true if all product positions equal and/or warehouse contain some another positions. Return if false
     * at least one position not exists.
     */
    private boolean isProductPositionsEquals(List<ProductLineGroup<OrderProductLine>> orderGroupList,
                                             List<ProductLineGroup<WarehouseProductLine>> warehouseGroupList) {
        Set<Product> ordSet = orderGroupList.stream().map(ProductLineGroup::getKey).collect(Collectors.toSet());
        Set<Product> warSet = warehouseGroupList.stream().map(ProductLineGroup::getKey).collect(Collectors.toSet());
        return warSet.containsAll(ordSet);
    }

    /**
     * Internal oversell check for all order products.
     *
     * @param orderGroupList     groups that represent all needed products.
     * @param warehouseGroupList groups that represent all available products.
     * @return optional exception that must be thrown if optional not empty.
     */
    private Optional<ProductsIsOversellsException> isProductQuantityEnough(List<ProductLineGroup<OrderProductLine>> orderGroupList,
                                                                           List<ProductLineGroup<WarehouseProductLine>> warehouseGroupList) {
        for (ProductLineGroup<OrderProductLine> productGroup : orderGroupList) {
            Product key = productGroup.getKey();
            ProductLineGroup<WarehouseProductLine> warehouseGroup = warehouseGroupList.stream().filter(g -> g.getKey().equals(key)).findFirst().get();
            int productSum = productGroup.getList().stream().mapToInt(AbstractProductLine::getQuantity).sum();
            int warehouseStock = warehouseGroup.getList().stream().mapToInt(AbstractProductLine::getQuantity).sum();
            if (productSum > warehouseStock)
                return Optional.of(new ProductsIsOversellsException(productSum, warehouseStock));
        }
        return Optional.empty();
    }

    /**
     * Retrieves {@code ProductLineGroup} from list of product line groups.
     *
     * @param groupList list of {@code ProductLineGroup} groups to retrieve.
     * @param key       any instance of {@code Product} class.
     * @param <T>       all child class of {@code AbstractProductLine}.
     * @return optional {@code ProductLineGroup} of specified product.
     */
    private <T extends AbstractProductLine> Optional<ProductLineGroup<T>> getProductGroupByKey(List<ProductLineGroup<T>> groupList,
                                                                                               Product key) {
        return groupList.stream().filter(group -> group.getKey().equals(key)).findFirst();
    }

    /**
     * Gets {@code List<StoreOrder>} with status {@code OrderStatus.NEW} from order repository
     * and make {@code List<ProductLineGroup>} from it.
     *
     * @return {@code List<ProductLineGroup>} by {@code WarehouseProductLine} or empty list,
     * if there is not any new orders.
     */
    private List<ProductLineGroup<OrderProductLine>> groupOrderLinesByProduct() {
        List<StoreOrder> orders = orderRepository.findByOrderStatus(OrderStatus.NEW);

        return orders.stream()
                .flatMap(ord -> ord.getProductLines().stream())
                .collect(Collectors.groupingBy(AbstractProductLine::getProduct))
                .entrySet()
                .stream()
                .map(entry -> new ProductLineGroup<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Gets {@code List<Warehouse>} from warehouse repository
     * and make {@code List<ProductLineGroup>} from it using flat map.
     *
     * @return {@code List<ProductLineGroup>} by {@code WarehouseProductLine} or empty list,
     * if all warehouse out of stock.
     */
    private List<ProductLineGroup<WarehouseProductLine>> groupWarehouseLinesByProduct() {
        List<Warehouse> warehouses = warehouseRepository.findAll();

        return warehouses.stream()
                .flatMap(ord -> ord.getProductLines().stream())
                .collect(Collectors.groupingBy(AbstractProductLine::getProduct))
                .entrySet()
                .stream()
                .map(entry -> new ProductLineGroup<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Group order lines by store and transform it into {@code AssignOrderLine}.
     *
     * @param lines input lines from all stores, but with one product.
     * @return map of grouped {@code AssignOrderLine} by {@code Store}.
     */
    private Map<Store, List<AssignOrderLine>> groupOrderLinesByStore(List<OrderProductLine> lines) {
        List<AssignOrderLine> collect = lines.stream()
                .map(line -> new AssignOrderLine(line.getProduct(), line.getQuantity(), line.getStoreOrder(), 0)).collect(Collectors.toList());
        return collect.stream().collect(Collectors.groupingBy(e -> e.getStoreOrder().getStore()));

    }

    /**
     * First caching of distances from warehouse to stores using distance repository.
     * @param warehouseGroupCatalog groups which contains all warehouse.
     * @return map of warehouse and distance list between any store and this one.
     */
    private Map<Warehouse, List<Distance>> cacheDistances(List<ProductLineGroup<WarehouseProductLine>> warehouseGroupCatalog) {
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
