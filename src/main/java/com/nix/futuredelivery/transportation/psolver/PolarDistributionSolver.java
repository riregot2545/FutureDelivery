package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class PolarDistributionSolver {
    private final Warehouse center;
    private final Set<Store> points;

    public List<StationPoint> resolvePolar() {
        List<StationPoint> sortedPoints = new ArrayList<>();
        for (Store store : points) {
            StationPoint point = new StationPoint(
                    getAngleBetweenLocations(center.getAddress().getPointLocation(), store.getAddress().getPointLocation()),
                    store,
                    center,
                    null,
                    Volume.empty()
            );
            sortedPoints.add(point);
        }
        sortedPoints.sort(Comparator.comparingDouble(StationPoint::getAngle));
        return sortedPoints;
    }

    private double getAngleBetweenLocations(Location center, Location point) {

        double differLong = Math.toRadians(point.getLongitude() - center.getLongitude());

        double centerLatRadians = Math.toRadians(center.getLatitude());
        double pointLatRadians = Math.toRadians(point.getLatitude());

        double y = Math.sin(differLong) * Math.cos(pointLatRadians);
        double x = Math.cos(centerLatRadians) * Math.sin(pointLatRadians) -
                Math.sin(centerLatRadians) * Math.cos(pointLatRadians) * Math.cos(differLong);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }
}
