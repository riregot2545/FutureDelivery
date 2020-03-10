package com.nix.futuredelivery.controller;

<<<<<<< HEAD
import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.Waybill;
import com.nix.futuredelivery.repository.RouteRepository;
import com.nix.futuredelivery.service.AdministratorService;
import lombok.RequiredArgsConstructor;
=======
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
>>>>>>> origin/master
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
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
    public List<Warehouse> getWarehousesState()
    {
        return administratorService.getWarehousesState();
=======

public class AdministratorController {

    @GetMapping("try")
    public String hasAccess(Authentication authentication){
        return "yess";
>>>>>>> origin/master
    }
}
