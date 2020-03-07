package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Waybill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@IdClass(WaybillProductLineId.class)
public class WaybillProductLine extends AbstractProductLine {
    @ManyToOne(cascade = CascadeType.ALL)
    @Id
    private Waybill waybill;

    private boolean isDelivered;

    public WaybillProductLine(Product product, int quantity, Waybill waybill, boolean isDelivered) {
        super(product, quantity);
        this.waybill = waybill;
        this.isDelivered = isDelivered;
    }
}
