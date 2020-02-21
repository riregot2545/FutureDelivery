package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Car;
import com.nix.futuredelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {


}
