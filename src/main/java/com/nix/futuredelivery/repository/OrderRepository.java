package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order,Long> {
}
