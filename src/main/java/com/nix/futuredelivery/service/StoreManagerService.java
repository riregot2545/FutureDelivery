package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.exceptions.NoStationException;
import com.nix.futuredelivery.repository.StoreManagerRepository;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private boolean storeHasOrder(StoreManager storeManager, StoreOrder storeOrder){
        return storeManager.getStore().getOrders().contains(storeOrder);
    }

    private boolean managerHasStore(StoreManager manager){
        return manager.getStore()!=null;
    }

    @Transactional
    public void saveStoreManager(StoreManager manager){
        String password = manager.getPassword();
        manager.setPassword("{noop}"+password);
        storeManagerRepository.save(manager);
    }

    @Transactional
    public List<OrderProductLine> getProductLines(Long userId, Long orderId) {
        StoreManager manager = storeManagerRepository.findById(userId).orElseThrow(() -> new IllegalStateException("no"));
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("no order exists with id +"+orderId));
        if(!storeHasOrder(manager, storeOrder)) throw new IllegalStateException("store with id "+ manager.getStore().getId()+" has no order with id +"+orderId);
        return storeOrder.getProductLines();
    }

    @Transactional
    public void makeNewOrder(Long id, List<OrderProductLine> productLines) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new IllegalStateException("no"));
        Store store = manager.getStore();
        productService.createOrder(productLines, store);
    }

    @Transactional
    public void deleteOrder(Long id, Long orderId) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new IllegalStateException("no"));
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("no order exists with id +"+orderId));
        if(!storeHasOrder(manager, storeOrder)) throw new IllegalStateException("store with id "+ manager.getStore().getId()+" has no order with id +"+orderId);
        storeOrderRepository.deleteById(orderId);
    }

    public List<StoreOrder> getOrders(Long id) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new IllegalStateException("no"));
        if(!managerHasStore(manager)) throw new NoStationException();
        return manager.getStore().getOrders();
    }
}
