package com.nix.futuredelivery.transportation.model;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.value.AbstractProductLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Transportation model class that implements {@code KeyListGroup} interface with product as key
 * and {@code AbstractProductLine} as list.
 *
 * @param <T> any list elements of that extends {@code AbstractProductLine}.
 */
public class ProductLineGroup<T extends AbstractProductLine> implements KeyListGroup<Product, List<T>> {

    private final Product product;
    private final List<T> lines;

    /**
     * Constructs product group from key and specified product lines
     * @param product product used as key.
     * @param lines list of {@code AbstractProductLine}.
     */
    public ProductLineGroup(Product product, List<T> lines) {
        this.product = product;
        this.lines = lines;
    }

    /**
     * Constructs product group from key and empty list of product lines
     * @param product product used as key.
     */
    public ProductLineGroup(Product product) {
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
}
