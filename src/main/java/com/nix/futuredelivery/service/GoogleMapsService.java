package com.nix.futuredelivery.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.value.AddressFormatter;
import com.nix.futuredelivery.exceptions.DistanceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class GoogleMapsService {

    @Value("${third-party.google.api-key}")
    private String API_KEY;

    public double getDistanceBetweenCords(Address addressFrom, Address addressTo) throws InterruptedException, ApiException, IOException, DistanceNotFoundException {
        GeoApiContext apiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        LatLng cordsFrom = new LatLng(addressFrom.getPointLocation().getLatitude(), addressFrom.getPointLocation().getLongitude());
        LatLng cordsTo = new LatLng(addressTo.getPointLocation().getLatitude(), addressTo.getPointLocation().getLongitude());


        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(apiContext);
        DistanceMatrix result = req.origins(cordsFrom)
                .destinations(cordsTo)
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();

        try {
            double distance = result.rows[0].elements[0].distance.inMeters / 1000D;

            return distance;
        } catch (NullPointerException ex) {
            throw new DistanceNotFoundException(addressFrom, addressTo);
        }
    }

    public double getDistanceBetweenNativeAddress(Address addressFrom, Address addressTo) throws InterruptedException, ApiException, IOException, DistanceNotFoundException {
        GeoApiContext apiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(apiContext);

        DistanceMatrix result = req.origins(AddressFormatter.toGoogleAddress(addressFrom))
                .destinations(AddressFormatter.toGoogleAddress(addressTo))
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();

        try {
            double distance = result.rows[0].elements[0].distance.inMeters / 1000D;

            return distance;
        } catch (NullPointerException ex) {
            throw new DistanceNotFoundException(addressFrom, addressTo);
        }
    }
}
