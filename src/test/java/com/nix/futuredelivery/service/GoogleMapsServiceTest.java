package com.nix.futuredelivery.service;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Address;
import com.nix.futuredelivery.entity.value.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GoogleMapsServiceTest {

    @Autowired
    private GoogleMapsService mapsService;

    @Test
    void getDistanceByCords() throws InterruptedException, ApiException, IOException {
        Address addressCenter = new Address(null, "", "", "", "", "", "",
                new Location(49.9997807, 36.2460375));

        Address address1 = new Address(null, "", "", "", "", "", "",
                new Location(50.010869, 36.234536));

        double distanceBetweenCords = mapsService.getDistanceBetweenCords(addressCenter, address1);
        assertEquals(2.066, distanceBetweenCords, 0.000001);
    }

    @Test
    void getDistanceByNative() throws InterruptedException, ApiException, IOException {
        Address addressCenter = new Address(null, "Богдана Хмельницкого ул., 24", "", "Переяслав",
                "Киевская область", "Украина", "08400",
                new Location(0, 0));

        Address address1 = new Address(null, "ул. Магдебурзкого права, 23", "", "Переяслав",
                "Киевская область", "Украина", "08400",
                new Location(0, 0));

        double distance = mapsService.getDistanceBetweenNativeAddress(addressCenter, address1);
        assertEquals(0.459, distance, 0.000001);
    }
}