package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.StoreOrder;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.value.AbstractProductLine;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.repository.StoreOrderRepository;
import com.nix.futuredelivery.service.StoreManagerService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@RestController
@PreAuthorize("hasAuthority('STORE_MANAGER')")
@RequestMapping("/store_manager")
public class StoreManagerController {
    private StoreManagerService storeManagerService;

    public StoreManagerController(StoreManagerService storeManagerService) {
        this.storeManagerService = storeManagerService;
    }

    @GetMapping("/{orderId}")
    public List<OrderProductLine> getProductLine(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getProductLines(user.getId(), orderId);
    }
    @GetMapping("/products")
    public Map<String, List<BigDecimal>> getAvaliableProducts(){
        return storeManagerService.getProducts();
    }
    @GetMapping()
    public List<StoreOrder> getOrders(Authentication authentication, LocalDate date){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getOrders(user.getId(), date);
    }

    @PostMapping()
    public void setOrder(Authentication authentication, @RequestBody  List<OrderProductLine> productLines){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.makeNewOrder(user.getId(), productLines);
    }
    @PatchMapping("/{orderId}")
    public void editOrder(Authentication authentication, @PathVariable Long orderId, @RequestBody  List<OrderProductLine> productLines){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        List<OrderProductLine> correct = new ArrayList<>();
        for (AbstractProductLine wrongLine:productLines) {
            correct.add(new OrderProductLine(wrongLine.getProduct(), wrongLine.getQuantity()));
        }
        storeManagerService.editOrder(user.getId(), orderId, correct);
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(Authentication authentication, @PathVariable Long orderId){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.deleteOrder(user.getId(), orderId);
    }

}
