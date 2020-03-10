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

import java.util.ArrayList;
import java.util.List;

@Data
@RestController
@PreAuthorize("hasAuthority('STORE_MANAGER')")
@RequestMapping("/store_manager")
public class StoreManagerController {
    private StoreManagerService storeManagerService;

    public StoreManagerController(StoreManagerService storeManagerService) {
        this.storeManagerService = storeManagerService;
    }

    @GetMapping("/{orderId}/get_product_lines")
    public List<OrderProductLine> getProductLine(Authentication authentication, @PathVariable Long orderId) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getProductLines(user.getId(), orderId);
    }

    @GetMapping("/get_orders")
    public List<StoreOrder> getOrders(Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getOrders(user.getId());
    }

    @PostMapping("/make_new_order")
    public void setOrder(Authentication authentication, @RequestBody  List<OrderProductLine> productLines){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        List<OrderProductLine> correct = new ArrayList<>();
        for (OrderProductLine wrongLine:productLines) {
            correct.add(new OrderProductLine(wrongLine.getProduct(), wrongLine.getQuantity()));
        }
        storeManagerService.makeNewOrder(user.getId(), correct);
    }

    @PostMapping("/{orderId}/edit")
    public void editOrder(Authentication authentication, @PathVariable Long orderId, @RequestBody  List<OrderProductLine> productLines){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        List<OrderProductLine> correct = new ArrayList<>();
        for (AbstractProductLine wrongLine:productLines) {
            correct.add(new OrderProductLine(wrongLine.getProduct(), wrongLine.getQuantity()));
        }
        storeManagerService.editOrder(user.getId(), orderId, correct);
    }

    @PostMapping("/delete_order/{orderId}")
    public void deleteOrder(Authentication authentication, @PathVariable Long orderId){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        storeManagerService.deleteOrder(user.getId(), orderId);
    }

}
