package com.nix.futuredelivery.controller;

import com.google.maps.errors.ApiException;
import com.nix.futuredelivery.entity.Store;
import com.nix.futuredelivery.entity.StoreManager;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.service.StoreManagerService;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@RestController
@RequestMapping("/store_manager")
public class StoreManagerController {
    private StoreManagerService storeManagerService;

    public StoreManagerController(StoreManagerService storeManagerService) {
        this.storeManagerService = storeManagerService;
    }

    @ApiOperation(value = "Register new store")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PostMapping("/store")
    public void registerStore(@RequestBody Store store) throws InterruptedException, ApiException, IOException {
        storeManagerService.saveStore(store);
    }

    @ApiOperation(value = "Register new store manager")
    @PostMapping("/register")
    public ResponseEntity registerStoreManager(@RequestBody StoreManager storeManager) {
        Long id = storeManagerService.saveStoreManager(storeManager);
        return new ResponseEntity<>("Warehouse manager saved successfully with id " + id, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all order lines by order number")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping("/{orderId}")
    public List<OrderProductLine> getProductLine(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getProductLines(user.getId(), orderId);
    }

    @ApiOperation(value = "Get a list of available products in warehouses")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping("/products")
    public List<AbstractProductLine> getAvailableProducts() {
        return storeManagerService.getProducts();
    }

    @ApiOperation(value = "Get all orders that were made since the date defined in header 'Date' ")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @GetMapping()
    public List<StoreOrder> getOrders(Authentication authentication, @RequestHeader("Date") String stringDate) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        LocalDateTime date = LocalDate.parse(stringDate, formatter).atStartOfDay();
        return storeManagerService.getOrders(user.getId(), date);
    }

    @ApiOperation(value = "Add new order")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PostMapping()
    public ResponseEntity<String> setOrder(Authentication authentication, @RequestBody List<OrderProductLine> productLines) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        Long id = storeManagerService.makeNewOrder(user.getId(), productLines);
        return new ResponseEntity<>("Store order saved successfully with id " + id, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @PatchMapping("/{orderId}")
    public void editOrder(Authentication authentication, @PathVariable Long orderId, @RequestBody List<OrderProductLine> productLines) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.editOrder(user.getId(), orderId, productLines);
    }

    @ApiOperation(value = "Delete the order by order number")
    @PreAuthorize("hasAuthority('STORE_MANAGER')")
    @DeleteMapping("/{orderId}")
    public void deleteOrder(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.deleteOrder(user.getId(), orderId);
    }

}