package com.nix.futuredelivery.controller;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.service.StoreManagerService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@RestController
@RequestMapping("/store_manager")
public class StoreManagerController {
    private StoreManagerService storeManagerService;

    public StoreManagerController(StoreManagerService storeManagerService) {
        this.storeManagerService = storeManagerService;
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PostMapping("/store")
    public void registrateWarehouse(@RequestBody Store store) throws InterruptedException, ApiException, IOException {
        storeManagerService.saveStore(store);
    }

    @PostMapping("/register")
    public void registerStoreManager(@RequestBody StoreManager storeManager) {
        storeManagerService.saveStoreManager(storeManager);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping("/{orderId}")
    public List<OrderProductLine> getProductLine(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getProductLines(user.getId(), orderId);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping("/products")
    public List<AbstractProductLine> getAvaliableProducts() {
        return storeManagerService.getProducts();
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping()
    public List<StoreOrder> getOrders(Authentication authentication, @RequestHeader("Date") String stringDate) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        LocalDateTime date = LocalDate.parse(stringDate, formatter).atStartOfDay();
        return storeManagerService.getOrders(user.getId(), date);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PostMapping()
    public void setOrder(Authentication authentication, @RequestBody List<OrderProductLine> productLines) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.makeNewOrder(user.getId(), productLines);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PatchMapping("/{orderId}")
    public void editOrder(Authentication authentication, @PathVariable Long orderId, @RequestBody List<OrderProductLine> productLines) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        List<OrderProductLine> correct = new ArrayList<>();
        for (AbstractProductLine wrongLine : productLines) {
            correct.add(new OrderProductLine(wrongLine.getProduct(), wrongLine.getQuantity()));
        }
        storeManagerService.editOrder(user.getId(), orderId, correct);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @DeleteMapping("/{orderId}")
    public void deleteOrder(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.deleteOrder(user.getId(), orderId);
    }

}