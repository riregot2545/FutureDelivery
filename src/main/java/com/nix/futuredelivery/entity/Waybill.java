package com.nix.futuredelivery.entity;

import com.nix.futuredelivery.entity.value.WaybillProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private Store store;

    @OneToMany(
            mappedBy = "waybill",
            cascade = CascadeType.ALL
    )
    private List<WaybillProductLine> productLines;

    @ManyToOne
    private Route route;
}
