package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Waybill;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
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
}
