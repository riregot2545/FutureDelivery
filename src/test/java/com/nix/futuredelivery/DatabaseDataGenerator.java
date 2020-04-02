package com.nix.futuredelivery;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseDataGenerator {

    private Random random = new Random();
    private PasswordEncoder passwordEncoder;

    public DatabaseDataGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private void encode(List<? extends SystemUser> users) {
        users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
    }
    public List<Car> cars(int countInGroup, int countOfGroups, double baseCapacity, double capacityGroupModificator,
                          double baseConsumption, double relativeConsumption) {
        List<Car> cars = new ArrayList<>(countInGroup);
        for (int i = 1; i <= countOfGroups; i++, baseCapacity *= capacityGroupModificator) {
            for (int j = 0; j < (countInGroup + (countInGroup * i) / 10D); j++) {
                Capacity capacity = new Capacity(new Volume(baseCapacity * i));
                Consumption consumption = new Consumption((baseConsumption + (baseConsumption * (i - 1)) / 10),
                        (relativeConsumption + (relativeConsumption * (i - 1)) / 10));

                Car car = new Car(null, "Model " + j + " Group " + i, capacity, consumption);
                cars.add(car);
            }
        }
        return cars;
    }

    public List<Driver> drivers(int count) {
        List<Driver> drivers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Driver driver = new Driver(null, "Driver " + i, "",
                    "driver" + i, "pass", "driver@mail.ua");
            drivers.add(driver);
        }
        encode(drivers);
        return drivers;
    }

    public List<Product> products(int count, double minVolume, double maxVolume, ProductCategory category) {
        List<Product> products = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Volume volume = new Volume(randDoubleBetween(minVolume, maxVolume));


            Product product = new Product(null, false, category, "Product " + (i + 1), new BigDecimal(random.nextInt(200) + 1), volume);
            products.add(product);
        }
        return products;
    }

    public List<Store> storeAndManagers(int count) {
        List<Store> stores = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            StoreManager storeManager = new StoreManager(null, "Store Manager " + i,
                    "", "storemanager" + i, "pass", "storemanager" + i + "@mail.ua", null);
            Address address = new Address(
                    null, "Store " + i + " addr", "",
                    "Kyiv", "Kyiv", "UA", "10000",
                    new Location(randDoubleBetween(20, 60), randDoubleBetween(20, 60)));
            storeManager.setPassword(passwordEncoder.encode(storeManager.getPassword()));
            Store store = new Store(null, address, "Store " + i, storeManager);
            storeManager.setStore(store);
            stores.add(store);
        }
        return stores;
    }

    public List<Warehouse> warehouseAndManagers(int count) {
        List<Warehouse> warehouses = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            WarehouseManager warehouseManager = new WarehouseManager(null, "Warehouse Manager " + i,
                    "", "warehousemanager" + i, "pass", "warehousemanager" + i + "@mail.ua", null);
            Address address = new Address(
                    null, "Warehouse " + i + " addr", "",
                    "Kyiv", "Kyiv", "UA", "10000",
                    new Location(randDoubleBetween(20, 60), randDoubleBetween(20, 60)));
            warehouseManager.setPassword(passwordEncoder.encode(warehouseManager.getPassword()));
            Warehouse store = new Warehouse(null, address, "Warehouse " + i, warehouseManager);
            warehouseManager.setWarehouse(store);
            warehouses.add(store);
        }
        return warehouses;
    }

    public List<Distance> distances(List<Warehouse> warehouses, List<Store> stores) {
        List<Distance> distances = new ArrayList<>();
        warehouses.forEach(w -> stores.forEach(s -> {
            double valueDistance = randDoubleBetween(0.2, 30);
            Distance distance = new Distance(w.getAddress(), s.getAddress(), valueDistance);
            distances.add(distance);
        }));

        stores.forEach(s1 -> stores.forEach(s2 -> {
            if (s1 != s2) {
                double valueDistance = randDoubleBetween(0.2, 30);
                distances.add(
                        new Distance(s1.getAddress(), s2.getAddress(), valueDistance)
                );
                distances.add(
                        new Distance(s2.getAddress(), s1.getAddress(), valueDistance)
                );
            }
        }));

        return distances;
    }

    public List<StoreOrder> orders(int count, List<Store> stores, List<Product> products, int minPositionsQuantity,
                                   int minProdQuantity, int maxProdQuantity) {
        List<StoreOrder> orders = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Store store = stores.get(random.nextInt(stores.size()));
            StoreOrder order = new StoreOrder(
                    null, OrderStatus.NEW, store, LocalDateTime.now(), new ArrayList<>());

            int randomProductInOrderCount = random.nextInt(products.size()) + minPositionsQuantity;
            List<Product> localProducts = new ArrayList<>(products);
            for (int j = 0; j < randomProductInOrderCount && localProducts.size() > 0; j++) {
                OrderProductLine productLine = new OrderProductLine(order);
                int randomProductIndex = random.nextInt(localProducts.size());
                productLine.setProduct(localProducts.get(randomProductIndex));
                productLine.setQuantity((int) randDoubleBetween(minProdQuantity, maxProdQuantity));
                order.getProductLines().add(productLine);
                localProducts.remove(randomProductIndex);
            }
            orders.add(order);
        }

        return orders;
    }

    public List<Warehouse> warehouseStock(List<Warehouse> warehouses, List<StoreOrder> orders, double productDispersion) {
        Map<Product, List<OrderProductLine>> productMap = orders.stream().flatMap(o -> o.getProductLines().stream())
                .collect(Collectors.groupingBy(AbstractProductLine::getProduct));
        productMap.forEach((product, lines) -> {
            int minimalQuantity = lines.stream().mapToInt(AbstractProductLine::getQuantity).sum();
            int realQuantity = (int) (minimalQuantity * (1 + productDispersion));

            for (int i = 0; realQuantity > 0; i++) {
                if (i == warehouses.size())
                    i = 0;
                Warehouse warehouse = warehouses.get(i);
                if (warehouse.getProductLines() == null)
                    warehouse.setProductLines(new ArrayList<>());

                int localQuantity = (int) randDoubleBetween(0, realQuantity + 20);
                localQuantity = Math.min(localQuantity, realQuantity);

                if (localQuantity > 0) {

                    Optional<WarehouseProductLine> lineOptional = warehouse.getProductLines().stream()
                            .filter(line -> line.getProduct().equals(product))
                            .findFirst();

                    if (lineOptional.isPresent()) {
                        lineOptional.get().setQuantity(lineOptional.get().getQuantity() + localQuantity);
                    } else {
                        WarehouseProductLine productLine = new WarehouseProductLine(warehouse);
                        productLine.setQuantity(localQuantity);
                        productLine.setProduct(product);

                        warehouse.getProductLines().add(productLine);
                    }
                    realQuantity -= localQuantity;
                }
            }
        });

        return warehouses;
    }

    private double randDoubleBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}