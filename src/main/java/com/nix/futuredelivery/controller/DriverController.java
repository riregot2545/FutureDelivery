package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.service.DriverService;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
@PreAuthorize("hasAuthority('DRIVER')")
@RequestMapping("/driver")
public class DriverController {
    private DriverService driverService;

    public DriverController(DriverService driverService){
        this.driverService = driverService;
    }
    @GetMapping("/get_all_routes")
    public List<Route> getDriversRoute(Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return driverService.getDriversRoutes(user.getId());
    }
    @PostMapping("/save_routes")
    public List<Route> addDriversRoute(@RequestBody List<Route> routes, Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return driverService.saveDriversRoute(routes, user.getId());
    }
}