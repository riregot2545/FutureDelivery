package com.nix.futuredelivery.transportation.vrpsolver;

import com.nix.futuredelivery.entity.Address;
import lombok.Data;

@Data
public class Station {

    private double x;
    private double y;

    private Address address;
    private boolean isWarehouse;

    public Station(Address address, boolean isWarehouse) {
        this.x = address.getPointLocation().getLatitude();
        this.y = address.getPointLocation().getLongitude();
        this.address = address;
        this.isWarehouse = isWarehouse;
    }
}