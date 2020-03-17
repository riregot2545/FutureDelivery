package com.nix.futuredelivery.transportation.model;

import java.util.List;

/**
 * Transportation model interface used for list groups building.
 *
 * @param <K> any key.
 * @param <T> any list.
 */
public interface KeyListGroup<K, T extends List<?>> {
    /**
     * Return specified group key
     * @return group key.
     */
    K getKey();

    /**
     * Returns the list value to which the key is mapped.
     * @return list of group values.
     */
    T getList();
}
