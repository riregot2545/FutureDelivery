package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    void editProductsOfWarehouse(List<WarehouseProductLine> lines, Warehouse warehouse) {
        lines.stream().filter(line -> warehouseContainsProduct(line.getProduct(), warehouse)).forEach(line -> updateWarehouseLine(line, warehouse));
    }
}
