package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoProductException;
import com.nix.futuredelivery.exceptions.WrongQuantityException;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WarehouseRepository warehouseRepository;
    private StoreOrderRepository storeOrderRepository;

    public ProductService(ProductRepository productRepository, WarehouseRepository warehouseRepository, StoreOrderRepository storeOrderRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.storeOrderRepository = storeOrderRepository;
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NoProductException(productId));
    }


    private int countProductInWarehouse(Product product, List<Warehouse> warehouses) {
        int quantity = 0;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.warehouseContainsProduct(product))
                quantity += warehouse.getWarehouseProductLine(product).getQuantity();
        }
        return quantity;
    }

    private int countProductInOrders(Product product, List<StoreOrder> orders) {
        int quantity = 0;
        for (StoreOrder order : orders) {
            if (order.containsProduct(product))
                quantity += order.getLineByProduct(product).getQuantity();
        }
        return quantity;
    }

    void addProductsToWarehouse(List<Product> lines, Warehouse warehouse) {
        List<WarehouseProductLine> productLines = lines.stream()
                .map(Product::getId)
                .map(this::getProduct)
                .filter(product -> !warehouse.warehouseContainsProduct(product))
                .map(product -> new WarehouseProductLine(product, 0, warehouse))
                .collect(Collectors.toList());

        warehouse.getProductLines().addAll(productLines);
    }

    void createOrder(List<OrderProductLine> lines, Store store) {
        StoreOrder order = new StoreOrder(store, false, false);
        List<OrderProductLine> productLines = new ArrayList<>();
        for (OrderProductLine line : lines) {
            Product product = getProduct(line.getProduct().getId());
            if(line.getQuantity()>getProducts().get(product)) throw new WrongQuantityException(product.getId(), line.getQuantity());
            productLines.add(new OrderProductLine(product, line.getQuantity(), order));
        }
        order.setProductLines(productLines);
        store.addOrder(order);
    }

    void editProductsOfWarehouse(List<WarehouseProductLine> lines, Warehouse warehouse) {
        lines.forEach(line -> {
            if (warehouse.warehouseContainsProduct(line.getProduct())) {
                warehouse.setWarehouseLineQuantity(line);
            } else {
                warehouse.getProductLines().add(new WarehouseProductLine(line.getProduct(), line.getQuantity(), warehouse));
            }
        });
    }

    void editStoreOrder(StoreOrder storeOrder, List<OrderProductLine> productLines) {
        for (OrderProductLine productLine : productLines) {
            if(productLine.getQuantity()>getProducts().get(productLine.getProduct()))
                throw new WrongQuantityException(productLine.getProduct().getId(), productLine.getQuantity());
            storeOrder.setOrderLineQuantity(productLine);
        }
    }

    Map<Product, List<Integer>> getProducts() {

        Map<Product, Map<String, >> menu = new HashMap<>();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<StoreOrder> orders = storeOrderRepository.findByisDistributedFalse();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            int quantity = countProductInWarehouse(product, warehouses) - countProductInOrders(product, orders);
            if (quantity < 0) throw new WrongQuantityException(product.getId(), quantity);
            if (quantity == 0) continue;
            List<Integer> quantityPrice = new ArrayList<>();
            quantityPrice.add(quantity); quantityPrice.add(product.getPrice());
            menu.put(product, quantityPrice);
        }
        return menu;
    }

}
