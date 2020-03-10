package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdministratorController {
    AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @GetMapping("/getActiveRoutes")
    public List<Route> getActiveRoutes() {
        return administratorService.getActiveRoutes();
    }

    @GetMapping("/getWarehousesState")
    public List<Warehouse> getWarehousesState() {
        return administratorService.getWarehousesState();
    }

    @GetMapping("try")
    public String hasAccess(Authentication authentication) {
        return "yess";
    }
}
