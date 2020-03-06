package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.value.AbstractProductLine;

import java.util.ArrayList;
import java.util.List;

public class ProductKeyListGroup<T extends AbstractProductLine> implements KeyListGroup<Product, List<T>> {

    private final Product product;
    private final List<T> lines;

    public ProductKeyListGroup(Product product, List<T> lines) {
        this.product = product;
        this.lines = lines;
    }

    public ProductKeyListGroup(Product product) {
        this.product = product;
        this.lines = new ArrayList<>();
    }

    @Override
    public Product getKey() {
        return product;
    }

    @Override
    public List<T> getList() {
        return lines;
    }

    public void add(T item) {
        lines.add(item);
    }

    public void remove(T item) {
        lines.remove(item);
    }
}
