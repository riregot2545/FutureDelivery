package com.nix.futuredelivery;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.*;
import com.nix.futuredelivery.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DbTestDataFilling {


    @Autowired
    private DistanceRepository distanceRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private StoreOrderRepository orderRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private WaybillRepository waybillRepository;

    private Random random = new Random();

    void dbTestDataFilling() {
        //store storeManager x3 +
        //store x3 +
        //warehouse storeManager x3 +
        //warehouse x3 +
        //distance between warehouse and store (T-solver) +
        //distance between store and store (VLP) +
        //product category x1 +
        //product x3 +
        //warehouse product lines xn +
        //store orders x3 +
        //driver x3 +
        //car x3 +
    }

    @BeforeAll
    void fillBasesTables(){
        storeAndManagersFilling();
        warehouseAndManagersFilling();
        productCategoryFilling();
        productFilling();
        carAndDriverFilling();
        createOrdersForStores();
        distanceRandomFilling();
    }

    void storeAndManagersFilling() {
        StoreManager storeManager = new StoreManager();
        storeManager.setFirstName("Store Manager 1");
        storeManager.setEmail("Store_manager1@mail.ua");
        storeManager.setLogin("Store_manager1");
        storeManager.setPassword("password");

        Store store = new Store(storeManager);
        store.setName("Store 1");
        store.setAddress(new Address(
                null, "store 1 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(50.50, 60.60)
        ));

        storeRepository.save(store);

        StoreManager storeManager2 = new StoreManager();
        storeManager2.setFirstName("Store Manager 2");
        storeManager2.setEmail("Store_manager2@mail.ua");
        storeManager2.setLogin("Store_manager2");
        storeManager2.setPassword("password");

        Store store2 = new Store(storeManager2);
        store2.setName("Store 2");
        store2.setAddress(new Address(
                null, "store 2 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(50.50, 60.60)
        ));

        storeRepository.save(store2);

        StoreManager storeManager3 = new StoreManager();
        storeManager3.setFirstName("Store Manager 3");
        storeManager3.setEmail("Store_manager3@mail.ua");
        storeManager3.setLogin("Store_manager3");
        storeManager3.setPassword("password");

        Store store3 = new Store(storeManager3);
        store3.setName("Store 3");
        store3.setAddress(new Address(
                null, "store 3 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(50.50, 60.60)
        ));

        storeRepository.save(store3);
    }

    void warehouseAndManagersFilling() {
        WarehouseManager warehouseManager1 = new WarehouseManager();
        warehouseManager1.setFirstName("Warehouse Manager1");
        warehouseManager1.setEmail("Warehouse_manager1@mail.ua");
        warehouseManager1.setLogin("Warehouse_manager1");
        warehouseManager1.setPassword("password");

        Warehouse warehouse1 = new Warehouse(warehouseManager1, null);
        warehouse1.setName("Warehouse 1");
        warehouse1.setAddress(new Address(
                null, "warehouse 1 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(30.30, 30.30)
        ));

        warehouseRepository.save(warehouse1);

        WarehouseManager warehouseManager2 = new WarehouseManager();
        warehouseManager2.setFirstName("Warehouse Manager2");
        warehouseManager2.setEmail("Warehouse_manager2@mail.ua");
        warehouseManager2.setLogin("Warehouse_manager2");
        warehouseManager2.setPassword("password");

        Warehouse warehouse2 = new Warehouse(warehouseManager2, null);
        warehouse2.setName("Warehouse 2");
        warehouse2.setAddress(new Address(
                null, "warehouse 2 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(30.30, 30.30)
        ));

        warehouseRepository.save(warehouse2);

        WarehouseManager warehouseManager3 = new WarehouseManager();
        warehouseManager3.setFirstName("Warehouse Manager3");
        warehouseManager3.setEmail("Warehouse_manager3@mail.ua");
        warehouseManager3.setLogin("Warehouse_manager3");
        warehouseManager3.setPassword("password");

        Warehouse warehouse3 = new Warehouse(warehouseManager3, null);
        warehouse3.setName("Warehouse 3");
        warehouse3.setAddress(new Address(
                null, "warehouse 3 addr", "",
                "Kyiv", "Kyiv", "UA", "10000",
                new Location(30.30, 30.30)
        ));

        warehouseRepository.save(warehouse3);
    }

    void distanceRandomFilling() {

        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Store> stores = storeRepository.findAll();

        warehouses.forEach(w -> stores.forEach(s -> {
            double valueDistance = random.nextInt(200) * Math.PI;
            Distance distance = new Distance();
            distance.setAddressFrom(w.getAddress());
            distance.setAddressTo(s.getAddress());
            distance.setDistance(valueDistance);
            distanceRepository.save(
                    distance
            );
        }));

        stores.forEach(s1 -> stores.forEach(s2 -> {
            double valueDistance = random.nextInt(200) * Math.PI;
            distanceRepository.save(
                    new Distance(s1.getAddress(), s2.getAddress(), valueDistance)
            );
            distanceRepository.save(
                    new Distance(s2.getAddress(), s1.getAddress(), valueDistance)
            );
        }));
    }

    void productCategoryFilling() {
        ProductCategory category = new ProductCategory(null, "testCategory", null);
        productCategoryRepository.save(category);
    }

    void productFilling() {
        productCategoryRepository.findByName("testCategory").ifPresent(cat -> {
            for (int i = 0; i < 10; i++) {
                Product product = new Product(
                        null, false, cat, "product "+(i+1), new BigDecimal(random.nextInt(200) * Math.PI),
                        new Volume(random.nextInt(3) * Math.PI+random.nextInt(3) * Math.PI+1)
                );
                productRepository.save(product);
            }
        });
    }

    void createOrdersForStores() {
        List<Store> stores = storeRepository.findAll();
        stores.forEach(s -> orderRepository.save(new StoreOrder(
                null, OrderStatus.NEW ,s, LocalDateTime.now(), null)));
    }

    void carAndDriverFilling(){
        Car car1 = new Car(null, "Car 1", new Capacity(new Volume(1000)), new Consumption(15, 0.00025));
        Driver driver1 = new Driver(null,"Driver 1","",
                "driver1","password","driver1@mail.ua");
        carRepository.save(car1);
        driverRepository.save(driver1);

        Car car2 = new Car(null, "Car 2", new Capacity(new Volume(1000)), new Consumption(15, 0.00025));
        Driver driver2 = new Driver(null,"Driver 2","",
                "driver2","password","driver2@mail.ua");
        carRepository.save(car2);
        driverRepository.save(driver2);

        Car car3 = new Car(null, "Car 3", new Capacity(new Volume(1000)), new Consumption(15, 0.00025));
        Driver driver3 = new Driver(null,"Driver 3","",
                "driver3","password","driver3@mail.ua");
        carRepository.save(car3);
        driverRepository.save(driver3);
    }

    @Test
    @Transactional
    void randomFillOrderLines() {
        List<StoreOrder> orders = orderRepository.findAll();
        orders.forEach(ord -> {
            List<Product> products = productRepository.findAll();


            int randomProductInOrderCount = random.nextInt(products.size()) + 1;
            for (int i = 0; i < randomProductInOrderCount; i++) {
                OrderProductLine productLine = new OrderProductLine(ord);
                int randomProductIndex = random.nextInt(products.size());
                productLine.setProduct(products.get(randomProductIndex));
                productLine.setQuantity(random.nextInt(100)+20);
                ord.getProductLines().add(productLine);
                products.remove(randomProductIndex);
            }
            orderRepository.save(ord);
        });
        TestTransaction.flagForCommit();
    }

    @Test
    @Transactional
    void randomFillWarehouseStock() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Product> originalProducts = productRepository.findAll();
        List<Product> requiredProducts = new ArrayList<>(originalProducts.subList(0,originalProducts.size()));
        int productCount = requiredProducts.size();
        int requiredCount = (int) Math.ceil((double) requiredProducts.size()/(double)warehouses.size());

        warehouses.forEach(war -> {

            int maxIndex = Math.min(requiredCount, requiredProducts.size());
            List<Product> requiredToInsert = new ArrayList<>(requiredProducts.subList(0,maxIndex));
            requiredProducts.removeAll(requiredToInsert);
            for (Product product : requiredToInsert) {
                WarehouseProductLine productLine = new WarehouseProductLine(war);
                productLine.setProduct(product);
                productLine.setQuantity(random.nextInt(1000) + 200);
                war.getProductLines().add(productLine);
            }

            int randomOtherProductsCount = random.nextInt(productCount-requiredCount);
            List<Product> productsToRandomInsert = new ArrayList<>(originalProducts.subList(0,originalProducts.size()));
            productsToRandomInsert.removeAll(requiredToInsert);
            for (int i = 0; i < randomOtherProductsCount; i++) {
                int randomIndex = random.nextInt(productsToRandomInsert.size());
                WarehouseProductLine productLine = new WarehouseProductLine(war);
                productLine.setProduct(productsToRandomInsert.get(randomIndex));
                productLine.setQuantity(random.nextInt(1000)+200);
                war.getProductLines().add(productLine);

                productsToRandomInsert.remove(randomIndex);
            }


            warehouseRepository.save(war);
        });
        TestTransaction.flagForCommit();
    }

}