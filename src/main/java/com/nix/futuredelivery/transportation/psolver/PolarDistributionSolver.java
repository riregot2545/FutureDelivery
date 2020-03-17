package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.transportation.psolver.model.StationPoint;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Class that contain logic for store sorting by wiper blade algorithm
 */
@AllArgsConstructor
public class PolarDistributionSolver {
    private final Warehouse center;
    private final Set<Store> points;

    /**
     * Method that iterate stores for calculation the angle between store and warehouse. After that method wrap
     * store by {@code StationPoint}. Based on it, result list of points sorts by angle.
     *
     * @return list of {@code StationPoint}.
     */
    public List<StationPoint> resolvePolar() {
        List<StationPoint> sortedPoints = new ArrayList<>();
        for (Store store : points) {
            StationPoint point = new StationPoint(
                    getAngleBetweenLocations(center.getAddress().getPointLocation(), store.getAddress().getPointLocation()),
                    store, center,null);
            sortedPoints.add(point);
        }
        sortedPoints.sort(Comparator.comparingDouble(StationPoint::getAngle));
        return sortedPoints;
    }

    /**
     * Calculation method that gets polar angle between two Earth locations. It can be
     * represent as getting angle between center of the circle and point on it.
     * @param center point that will be center of the circle.
     * @param point point that will represent as point on circle.
     * @return angle between two points on the circle
     */
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
