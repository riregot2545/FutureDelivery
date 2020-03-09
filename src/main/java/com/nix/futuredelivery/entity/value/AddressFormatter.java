package com.nix.futuredelivery.entity.value;

import com.nix.futuredelivery.entity.Address;

public class AddressFormatter {
    public static String toGoogleAddress(Address address) {
        return address.getAddressLine1() + "," +
                (address.getAddressLine2().isEmpty() ? "" : address.getAddressLine2() + ",") +
                address.getCity() + "," +
                address.getRegion() + "," +
                address.getCity() + "," +
                address.getZipCode();
    }
}
