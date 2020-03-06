package com.nix.futuredelivery.transportation.psolver;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.Capacity;
import com.nix.futuredelivery.entity.value.Location;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.Volume;
import com.nix.futuredelivery.transportation.TransportationAssigner;
import com.nix.futuredelivery.transportation.model.DistributionEntry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TransportationAssignerTest {
    private TransportationAssigner assigner;
    private Random random = new Random(2);

    @Test
    public void polarTestFakeData1() {
        List<Car> carList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            carList.add(new Car((long) i, "model" + i, new Capacity(new Volume(100 + i * 100))));
        }

        List<Driver> driverList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            driverList.add(new Driver((long) i, "Driver " + i, "", "driver" + i, "password", "driver" + i + "@email.ua"));
        }
        Warehouse warehouse = new Warehouse(
                null,
                new Address(null, "", "", "", "", "", "",
                        new Location(49.9997807, 36.2460375)),
                "warehouse",
                null
        );
        List<DistributionEntry> fakeEntries = new ArrayList<>();

        List<Location> storeLocations = new ArrayList<>();
        storeLocations.add(new Location(50.010869, 36.234536));
        storeLocations.add(new Location(49.999383, 36.278895));
        storeLocations.add(new Location(49.994377, 36.254351));
        storeLocations.add(new Location(49.980815, 36.250824));
        storeLocations.add(new Location(49.972129, 36.250508));
        storeLocations.add(new Location(49.972947, 36.218771));
        storeLocations.add(new Location(49.981325, 36.205412));
        storeLocations.add(new Location(49.999303, 36.202672));

        List<Product> productList = new ArrayList<>();
        int randomProductCount = random.nextInt(10) + 1;

        for (int i = 0; i < randomProductCount; i++) {
            productList.add(new Product((long) i, null, "product" + (i + 1), new BigDecimal(i), new Volume(1)));
        }

        for (int i = 0; i < storeLocations.size(); i++) {
            Location storeLocation = storeLocations.get(i);
            Store store = new Store((long) i,
                    new Address((long) i, "", "", "", "", "", "", storeLocation),
                    "store" + (i + 1),
                    null);

            List<OrderProductLine> orderProductLines = new ArrayList<>();
            randomProductCount = random.nextInt(3) + 1;
            Product product = productList.get(random.nextInt(productList.size()));
            for (int j = 0; j < randomProductCount; j++) {
                StoreOrder order = new StoreOrder(store, false, false);
                orderProductLines.add(new OrderProductLine(
                        product, random.nextInt(50) + 1, order
                ));
            }


            fakeEntries.add(new DistributionEntry(
                    new DistributionEntry.DistributionKey(store, product, warehouse),
                    orderProductLines
            ));

        }


        //seed = 2 all products volume=370
        assigner = new TransportationAssigner(carList, driverList, fakeEntries);
        assigner.assign();
    }


    @Test
    public void polarTestFakeData2() {
        List<Car> carList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int randomCarsInGroup = random.nextInt(4) + 1;
            for (int j = 0; j < randomCarsInGroup; j++) {
                carList.add(new Car((long) i, "model" + i, new Capacity(new Volume(100 + i * 100))));
            }
        }

        List<Driver> driverList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            driverList.add(new Driver((long) i, "Driver " + i, "", "driver" + i, "password", "driver" + i + "@email.ua"));
        }
        Warehouse warehouse = new Warehouse(
                null,
                new Address(null, "", "", "", "", "", "",
                        new Location(49.9997807, 36.2460375)),
                "warehouse",
                null
        );
        List<DistributionEntry> fakeEntries = new ArrayList<>();

        List<Location> storeLocations = new ArrayList<>();
        storeLocations.add(new Location(50.010869, 36.234536));
        storeLocations.add(new Location(49.999383, 36.278895));
        storeLocations.add(new Location(49.994377, 36.254351));
        storeLocations.add(new Location(49.980815, 36.250824));
        storeLocations.add(new Location(49.972129, 36.250508));
        storeLocations.add(new Location(49.972947, 36.218771));
        storeLocations.add(new Location(49.981325, 36.205412));
        storeLocations.add(new Location(49.999303, 36.202672));

        List<Product> productList = new ArrayList<>();
        int randomProductCount = random.nextInt(10) + 1;

        for (int i = 0; i < randomProductCount; i++) {
            productList.add(new Product((long) i, null, "product" + (i + 1),
                    new BigDecimal(i),
                    new Volume(random.nextInt(5) + 1)));
        }

        for (int i = 0; i < storeLocations.size(); i++) {
            Location storeLocation = storeLocations.get(i);
            Store store = new Store((long) i,
                    new Address((long) i, "", "", "", "", "", "", storeLocation),
                    "store" + (i + 1),
                    null);

            List<OrderProductLine> orderProductLines = new ArrayList<>();
            randomProductCount = random.nextInt(3) + 1;
            Product product = productList.get(random.nextInt(productList.size()));
            for (int j = 0; j < randomProductCount; j++) {
                StoreOrder order = new StoreOrder(store, false, false);
                order.setId((long) j);
                orderProductLines.add(new OrderProductLine(
                        product, random.nextInt(100) + 1, order
                ));
            }


            fakeEntries.add(new DistributionEntry(
                    new DistributionEntry.DistributionKey(store, product, warehouse),
                    orderProductLines
            ));
        }


        //seed = 2 all products volume = 2868.0
        assigner = new TransportationAssigner(carList, driverList, fakeEntries);
        assigner.assign();
    }

    @Test
    public void polarTestFakeData3() {
        List<Car> carList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int randomCarsInGroup = random.nextInt(4) + 1;
            for (int j = 0; j < randomCarsInGroup; j++) {
                carList.add(new Car((long) i, "model" + i, new Capacity(new Volume(100 + i * 100))));
            }
        }

        List<Driver> driverList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            driverList.add(new Driver((long) i, "Driver " + i, "", "driver" + i, "password", "driver" + i + "@email.ua"));
        }
        Warehouse warehouse = new Warehouse(
                null,
                new Address(null, "", "", "", "", "", "",
                        new Location(49.9997807, 36.2460375)),
                "warehouse",
                null
        );
        List<DistributionEntry> fakeEntries = new ArrayList<>();

        List<Location> storeLocations = new ArrayList<>();
        storeLocations.add(new Location(50.010869, 36.234536));
        storeLocations.add(new Location(49.999383, 36.278895));
        storeLocations.add(new Location(49.994377, 36.254351));
        storeLocations.add(new Location(49.980815, 36.250824));
        storeLocations.add(new Location(49.972129, 36.250508));
        storeLocations.add(new Location(49.972947, 36.218771));
        storeLocations.add(new Location(49.981325, 36.205412));
        storeLocations.add(new Location(49.999303, 36.202672));

        List<Product> productList = new ArrayList<>();
        int randomProductCount = random.nextInt(10) + 1;

        for (int i = 0; i < randomProductCount; i++) {
            productList.add(new Product((long) i, null, "product" + (i + 1),
                    new BigDecimal(i),
                    new Volume(random.nextInt(5) + 1)));
        }

        for (int i = 0; i < storeLocations.size(); i++) {
            Location storeLocation = storeLocations.get(i);
            Store store = new Store((long) i,
                    new Address((long) i, "", "", "", "", "", "", storeLocation),
                    "store" + (i + 1),
                    null);

            List<OrderProductLine> orderProductLines = new ArrayList<>();
            randomProductCount = random.nextInt(3) + 1;
            Product product = productList.get(random.nextInt(productList.size()));
            for (int j = 0; j < randomProductCount; j++) {
                StoreOrder order = new StoreOrder(store, false, false);
                order.setId((long) j);
                orderProductLines.add(new OrderProductLine(
                        product, random.nextInt(20) + 1, order
                ));
            }

            fakeEntries.add(new DistributionEntry(
                    new DistributionEntry.DistributionKey(store, product, warehouse),
                    orderProductLines
            ));

        }


        //seed = 2 all products volume = 728
        assigner = new TransportationAssigner(carList, driverList, fakeEntries);
        assigner.assign();
    }
}