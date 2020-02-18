package com.nix.futuredelivery.repository;

import com.nix.futuredelivery.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
