package com.nix.futuredelivery.transportation.vrpsolver;

import lombok.Data;

@Data
public class City {

    private double x;
    private double y;

    public City(Address address) {
        address.getPointLocation().getLo
        this.x = (double) (Math.random() * 500);
        this.y = (double) (Math.random() * 500);
    }

    public double distanceToCity(City city) {
        double x = Math.abs(getX() - city.getX());
        double y = Math.abs(getY() - city.getY());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

}