package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Waybill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @ManyToOne
    @Id
    private Waybill waybill;

    @ColumnDefault("false")
    private boolean isDelivered;
}
