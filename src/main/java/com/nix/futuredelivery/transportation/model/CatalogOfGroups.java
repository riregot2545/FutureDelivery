package com.nix.futuredelivery.transportation.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CatalogOfGroups<G extends KeyListGroup<?, ?>> {
    private final Map<Object, G> mapOfGroups;

    public CatalogOfGroups() {
        this.mapOfGroups = new HashMap<>();
    }

    public void add(G value) {
        mapOfGroups.put(value.getKey(), value);
    }

    public void remove(Object key) {
        mapOfGroups.remove(key);
    }

    public G get(Object key) {
        return mapOfGroups.get(key);
    }

    public boolean containsKey(Object key) {
        return mapOfGroups.containsKey(key);
    }

    public Set<?> keys() {
        return mapOfGroups.keySet();
    }

    public boolean containsCollectionOfKeys(Collection<?> keys) {
        for (Object key : keys) {
            if (!mapOfGroups.containsKey(key))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CatalogOfGroups{" +
                "mapOfGroups=" + mapOfGroups +
                '}';
    }
}
