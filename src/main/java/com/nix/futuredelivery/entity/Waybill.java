package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.WaybillProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Waybill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private StoreOrder storeOrder;

    @OneToMany(
            mappedBy = "waybill",
            cascade = CascadeType.ALL
    )
    private List<WaybillProductLine> productLines;

    @ManyToOne
    private Route route;

    private int deliveryQueuePlace;

    private BigDecimal productCost;
    private BigDecimal deliveryCost;

    private boolean isStoreMain;

    public void updateProductCost() {
        BigDecimal cost = new BigDecimal(0);
        cost = productLines.stream()
                .map(line -> line.getProduct().getPrice().multiply(new BigDecimal(line.getQuantity())))
                .reduce(cost, BigDecimal::add);
        productCost = cost;
    }

}
