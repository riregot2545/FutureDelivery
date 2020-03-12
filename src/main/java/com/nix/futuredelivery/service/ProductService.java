package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoProductException;
import com.nix.futuredelivery.repository.ProductRepository;
import com.nix.futuredelivery.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WarehouseRepository warehouseRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NoProductException(productId));
    }

    private Map<Product, Integer> countProductsInWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Product> products = productRepository.findAll();
        for (Warehouse warehouse : warehouses) {

        }
        return null;
    }

    private Map.Entry<Product, Integer> countProductInWarehouse(Product product, List<Warehouse> warehouses) {
        int quantity = 0;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.warehouseContainsProduct(product))
                quantity += warehouse.getWarehouseProductLine(product).getQuantity();
        }
        return null;
    }

    private Map.Entry<Product, Integer> countProductIncludingOrders(Product product, List<StoreOrder> orders) {
        return null;
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
        StoreOrder order = new StoreOrder(store, true, false);
        List<OrderProductLine> productLines = new ArrayList<>();
        for (OrderProductLine line : lines) {
            Product product = getProduct(line.getProduct().getId());
            productLines.add(new OrderProductLine(product, 0, order));
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
        productLines.forEach(storeOrder::setOrderLineQuantity);
    }

    public Map<Product, Integer> getProducts() {

        List<Product> products = productRepository.findAll();

        return null;
    }

}
