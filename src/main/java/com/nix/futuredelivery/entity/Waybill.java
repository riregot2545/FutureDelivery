package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nix.futuredelivery.entity.value.WaybillProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @JsonIgnoreProperties({"productLines", "store"})
    @ManyToOne
    @NotNull(message = "Waybill order is null.")
    private StoreOrder storeOrder;

    @JsonIgnoreProperties({"waybill"})
    @OneToMany(
            mappedBy = "waybill",
            cascade = CascadeType.ALL)
    private List<WaybillProductLine> productLines;

    @ManyToOne
    @NotNull(message = "Waybill route is null.")
    private Route route;

    @ColumnDefault("0")
    private int deliveryQueuePlace;

    @NotNull(message = "Waybill product cost is null.")
    @Positive(message = "Waybill product cost is negative")
    private BigDecimal productCost;
    @NotNull(message = "Waybill delivery cost is null.")
    @Positive(message = "Waybill delivery cost is negative")
    private BigDecimal deliveryCost;

    @ColumnDefault("false")
    private boolean isStoreMain;

    public void updateProductCost() {
        BigDecimal cost = new BigDecimal(0);
        cost = productLines.stream()
                .map(line -> line.getProduct().getPrice().multiply(new BigDecimal(line.getQuantity())))
                .reduce(cost, BigDecimal::add);
        productCost = cost;
    }

}
