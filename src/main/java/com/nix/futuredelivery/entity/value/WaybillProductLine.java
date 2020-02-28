package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Waybill;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(WaybillProductLineId.class)
public class WaybillProductLine extends AbstractProductLine {
    @ManyToOne
    @Id
    private Waybill waybill;

    private boolean isDelivered;
}
