package com.nix.futuredelivery.service;

import com.nix.futuredelivery.entity.*;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.exceptions.NoOrderException;
import com.nix.futuredelivery.exceptions.NoOrderInStoreException;
import com.nix.futuredelivery.exceptions.NoPersonException;
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
        StoreManager manager = storeManagerRepository.findById(userId).orElseThrow(() -> new NoPersonException("Store manager", userId));
        if(!managerHasStore(manager)) throw new NoStationException(manager.getId());
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new NoOrderException(orderId));
        if(!storeHasOrder(manager, storeOrder)) throw new NoOrderInStoreException(manager.getStore().getId(), storeOrder.getId());
        return storeOrder.getProductLines();
    }

    @Transactional
    public void makeNewOrder(Long id, List<OrderProductLine> productLines) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Store manager", id));
        if(!managerHasStore(manager)) throw new NoStationException(manager.getId());
        productService.createOrder(productLines, manager.getStore());
    }

    @Transactional
    public void deleteOrder(Long id, Long orderId) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Store manager", id));
        if(!managerHasStore(manager)) throw new NoStationException(manager.getId());
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new NoOrderException(orderId));
        if(!storeHasOrder(manager, storeOrder)) throw new NoOrderInStoreException(manager.getStore().getId(), orderId);
        storeOrderRepository.deleteById(orderId);
    }

    @Transactional
    public List<StoreOrder> getOrders(Long id) {
        StoreManager manager = storeManagerRepository.findById(id).orElseThrow(() -> new NoPersonException("Store manager", id));
        if(!managerHasStore(manager)) throw new NoStationException(manager.getId());
        return manager.getStore().getOrders();
    }

    @Transactional
    public void editOrder(Long userId, Long orderId, List<OrderProductLine> productLines) {
        StoreManager manager = storeManagerRepository.findById(userId).orElseThrow(() -> new NoPersonException("Store manager", userId));
        if(!managerHasStore(manager)) throw new NoStationException(manager.getId());
        StoreOrder storeOrder = storeOrderRepository.findById(orderId).orElseThrow(() -> new NoOrderException(orderId));
        if(!storeHasOrder(manager, storeOrder)) throw new NoOrderInStoreException(manager.getStore().getId(), orderId);
        productService.editStoreOrder(storeOrder, productLines);
    }
}
