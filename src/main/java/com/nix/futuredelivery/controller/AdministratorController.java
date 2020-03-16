package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Notification;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.service.AdministratorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdministratorController {

    AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @GetMapping("/active_routes")
    public List<Route> getActiveRoutes() {
        return administratorService.getActiveRoutes();
    }

    @GetMapping("/warehouses_state")
    public List<Warehouse> getWarehousesState() {
        return administratorService.getWarehousesState();
    }

    @GetMapping("try")
    public String hasAccess(Authentication authentication) {
        return "yess";
    }

    @GetMapping("/notification")
    public Notification getNotidication() {
        return administratorService.getNotification();
    }
    @PostMapping("/confirm_product")
    public void confirmProduct(@RequestBody List<Product> productList)
    {
        administratorService.confirmProduct(productList);
    }
}
