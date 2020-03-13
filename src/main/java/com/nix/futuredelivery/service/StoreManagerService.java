package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.exceptions.*;
import com.nix.futuredelivery.repository.StoreManagerRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PreRemove;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreManagerService {

    private StoreManagerRepository storeManagerRepository;
    private ProductService productService;
    private StoreOrderRepository storeOrderRepository;


    public StoreManagerService(StoreManagerRepository storeManagerRepository, ProductService productService, StoreOrderRepository storeOrderRepository) {
        this.storeManagerRepository = storeManagerRepository;
        this.productService = productService;
        this.storeOrderRepository = storeOrderRepository;
    }


    private boolean storeHasOrder(StoreManager storeManager, StoreOrder storeOrder) {
        return storeManager.getStore().getOrders().contains(storeOrder);
    }
    private boolean managerHasStore(StoreManager manager) {
        return manager.getStore() != null;
    }

    @Transactional
    public List<OrderProductLine> getProductLines(Long userId, Long orderId) {
        StoreManager manager = storeManagerRepository.findById(userId).orElseThrow(() -> new NoPersonException("Store manager", userId));
        if (!managerHasStore(manager)) throw new NoStationException(manager.getId());
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new NoOrderException(orderId));
        if (!storeHasOrder(manager, storeOrder))
            throw new NoOrderInStoreException(manager.getStore().getId(), storeOrder.getId());
        return storeOrder.getProductLines();
    }

    @Transactional
    public void makeNewOrder(Long managerId, List<OrderProductLine> productLines) {
        StoreManager manager = storeManagerRepository.findById(managerId).orElseThrow(() -> new NoPersonException("Store manager", managerId));
        if (!managerHasStore(manager)) throw new NoStationException(manager.getId());
        productService.createOrder(productLines, manager.getStore());
    }

    @Transactional
    public void deleteOrder(Long managerId, Long orderId) {
        StoreOrder storeOrder = getOrder(managerId, orderId);
        if (storeOrder.isDistributed()) throw new OrderStateException(storeOrder.getId());
        storeOrderRepository.deleteById(orderId);
    }
    @Transactional
    public StoreOrder getOrder(Long id, Long orderId) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Store manager", id));
        if (!managerHasStore(manager)) throw new NoStationException(manager.getId());
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new NoOrderException(orderId));
        if (!storeHasOrder(manager, storeOrder)) throw new NoOrderInStoreException(manager.getStore().getId(), orderId);
        return storeOrder;
    }
    @Transactional
    public List<StoreOrder> getOrders(Long managerId, LocalDate date) {
        StoreManager manager = storeManagerRepository.findById(managerId).orElseThrow(() -> new NoPersonException("Store manager", managerId));
        if (!managerHasStore(manager)) throw new NoStationException(manager.getId());
        List<StoreOrder> orders = manager.getStore().getOrders();
        return orders.stream().filter(order -> order.getCreationDate().isAfter(ChronoLocalDateTime.from(date))).collect(Collectors.toList());
    }

    @Transactional
    public void editOrder(Long userId, Long orderId, List<OrderProductLine> productLines) {
        StoreOrder storeOrder = getOrder(userId, orderId);
        productService.editStoreOrder(storeOrder, productLines);
    }

    public Map<String, List<BigDecimal>> getProducts() {
        return productService.getProducts();
    }
}
