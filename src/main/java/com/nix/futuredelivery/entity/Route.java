package com.nix.futuredelivery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Driver driver;
    @ManyToOne
    private Car car;

    @OneToMany(mappedBy = "route")
    private List<Waybill> waybillList;
    @ManyToOne
    private Warehouse warehouse;
}