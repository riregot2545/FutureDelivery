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
    private boolean closed;
    @ManyToOne
    private Driver driver;
    @ManyToOne
    private Car car;

    @OneToMany(mappedBy = "route")
    private List<Waybill> waybillList;
    @ManyToOne
    private Warehouse warehouse;

    @Transient
    private List<Store> routePoints;

    private boolean isClosed;

    public Route(boolean isClosed, Driver driver, Car car){
        this.isClosed = isClosed;
        this.driver = driver;
        this.car = car;
    }
}
