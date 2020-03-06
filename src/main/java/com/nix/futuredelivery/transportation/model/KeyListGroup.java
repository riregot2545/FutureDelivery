package com.nix.futuredelivery.transportation.model;

import java.util.List;

public interface KeyListGroup<K, T extends List<?>> {
    K getKey();

    T getList();
}
