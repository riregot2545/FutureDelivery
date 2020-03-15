package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.exceptions.*;
import com.nix.futuredelivery.repository.StoreManagerRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreManagerService {

    private StoreManagerRepository storeManagerRepository;
    private ProductService productService;
    private StoreOrderRepository storeOrderRepository;
    private PasswordEncoder passwordEncoder;


    public StoreManagerService(StoreManagerRepository storeManagerRepository, ProductService productService, StoreOrderRepository storeOrderRepository, PasswordEncoder passwordEncoder) {
        this.storeManagerRepository = storeManagerRepository;
        this.productService = productService;
        this.storeOrderRepository = storeOrderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean storeHasOrder(StoreManager storeManager, StoreOrder storeOrder) {
        return storeManager.getStore().getOrders().contains(storeOrder);
    }
    private boolean managerHasStore(StoreManager manager) {
        return manager.getStore() != null;
    }

    @Transactional
    public void saveStoreManager(StoreManager manager) {
        String password = manager.getPassword();
        manager.setPassword(passwordEncoder.encode(password));
        storeManagerRepository.save(manager);
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
        StoreManager manager = storeManagerRepository.findById(managerId).orElseThrow(() -> new NoPersonException("Store manager", managerId));
        manager.getStore().getOrders().remove(storeOrder);
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
    public List<StoreOrder> getOrders(Long managerId, LocalDateTime date) {
        StoreManager manager = storeManagerRepository.findById(managerId).orElseThrow(() -> new NoPersonException("Store manager", managerId));
        if (!managerHasStore(manager)) throw new NoStationException(manager.getId());
        List<StoreOrder> orders = manager.getStore().getOrders();
        return orders.stream().filter(order -> order.getCreationDate().isAfter(date)).collect(Collectors.toList());
    }

    @Transactional
    public void editOrder(Long userId, Long orderId, List<OrderProductLine> productLines) {
        StoreOrder storeOrder = getOrder(userId, orderId);
        productService.editStoreOrder(storeOrder, productLines);
    }

    public List<AbstractProductLine> getProducts() {
        return productService.getProducts();
    }
}
