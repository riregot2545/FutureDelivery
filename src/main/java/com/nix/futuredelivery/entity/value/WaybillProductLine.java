package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Waybill;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WaybillProductLine extends AbstractProductLine {
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("documentId")
    private Waybill waybill;

    private boolean isDelivered;
}
