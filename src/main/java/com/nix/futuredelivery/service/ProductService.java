package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.ProductRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private boolean warehouseContainsProduct(Product product, Warehouse warehouse) {
        List<WarehouseProductLine> productLines = warehouse.getProductLines();
        return productLines.stream().map(AbstractProductLine::getProduct).anyMatch(warehouseProduct -> warehouseProduct.equals(product));
    }
    private boolean orderContainsProduct(Product product, StoreOrder storeOrder) {
        List<OrderProductLine> productLines = storeOrder.getProductLines();
        return productLines.stream().map(AbstractProductLine::getProduct).anyMatch(orderProduct -> orderProduct.equals(product));
    }

    private void setQuantity(List<OrderProductLine> withoutQuantity, List<OrderProductLine> withQuantity){
        IntStream.range(0, withoutQuantity.size()).forEach(i -> withoutQuantity.get(i).setQuantity(withQuantity.get(i).getQuantity()));
    }

    private Optional<WarehouseProductLine> getWarehouseProductLine(Warehouse warehouse, Product product) {
        for (WarehouseProductLine line : warehouse.getProductLines()) {
            if (line.getProduct().equals(product)) return Optional.of(line);
        }
        return Optional.empty();
    }

    private void updateWarehouseLine(WarehouseProductLine line, Warehouse warehouse){
        WarehouseProductLine oldLine = getWarehouseProductLine(warehouse, line.getProduct()).orElseThrow(()->new IllegalArgumentException("Warehouse doesn't have product "+line.getProduct()));
        oldLine.setQuantity(line.getQuantity());
    }

    void addProductsToWarehouse(List<Product> lines, Warehouse warehouse) {
        List<WarehouseProductLine> productLines = new ArrayList<>();

        lines.stream().map(Product::getId).
                map(productId -> productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product with id " + productId + " does not exist"))).
                filter(product -> !warehouseContainsProduct(product, warehouse)).
                forEach(product -> {
                    WarehouseProductLine productLine = new WarehouseProductLine(warehouse);
                    productLine.setProduct(product);
                    productLines.add(productLine);
                });

        warehouse.getProductLines().addAll(productLines);
    }
    Long createOrder(List<OrderProductLine> lines, Store store){
        StoreOrder order = new StoreOrder(store, false, false);
        List<OrderProductLine> productLines = new ArrayList<>();
        List<Product> products = lines.stream().map(AbstractProductLine::getProduct).collect(Collectors.toList());
        products.stream().map(Product::getId).
                map(productId -> productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product with id " + productId + " does not exist"))).
                forEach(product -> {
                    OrderProductLine productLine = new OrderProductLine(order);
                    productLine.setProduct(product);
                    productLines.add(productLine);
                });
        setQuantity(productLines, lines);
        order.setProductLines(productLines);
        store.addOrder(order);
        return order.getId();
    }

    void editProductsOfWarehouse(List<WarehouseProductLine> lines, Warehouse warehouse) {
        lines.stream().filter(line -> warehouseContainsProduct(line.getProduct(), warehouse)).forEach(line -> updateWarehouseLine(line, warehouse));
    }
}
