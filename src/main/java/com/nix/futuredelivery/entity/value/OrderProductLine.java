package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.StoreOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(OrderProductLineId.class)
public class OrderProductLine extends AbstractProductLine implements Serializable {
    @ManyToOne
    @Id
    private StoreOrder storeOrder;
}
