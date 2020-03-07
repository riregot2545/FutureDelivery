package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.value.OrderProductLine;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.service.StoreManagerService;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/get_product_lines")
    public List<OrderProductLine> getProductLine(Authentication authentication) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return storeManagerService.getProductLines(user.getId());
    }
}
