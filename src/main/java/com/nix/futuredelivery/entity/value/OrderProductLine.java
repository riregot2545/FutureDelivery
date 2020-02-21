package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class OrderProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @MapsId("documentId")
    private Order order;
}
