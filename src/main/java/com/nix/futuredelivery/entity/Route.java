package com.nix.futuredelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Route close state is null.")
    private boolean closed;
    @JsonIgnoreProperties({"authorities", "password"})
    @ManyToOne
    private Driver driver;

    @NotNull(message = "Route car is null.")
    @ManyToOne
    private Car car;

    @JsonIgnoreProperties("route")
    @OneToMany(mappedBy = "route")
    private List<Waybill> waybillList;

    @NotNull(message = "Route start warehouse is null.")
    @JsonIgnoreProperties({"warehouseManager", "productLines"})
    @ManyToOne
    private Warehouse warehouse;

    @JsonIgnoreProperties({"storeManager", "orders"})
    @Transient
    private List<Store> routePoints;

    private boolean isClosed;

    public Route(boolean isClosed, Driver driver, Car car){
        this.isClosed = isClosed;
        this.driver = driver;
        this.car = car;
    }
}
