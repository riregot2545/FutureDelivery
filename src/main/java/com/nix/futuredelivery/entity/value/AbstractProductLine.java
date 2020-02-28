package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractProductLine implements Serializable {
    @ManyToOne
    @Id
    private Product product;

    private int quantity;
}
