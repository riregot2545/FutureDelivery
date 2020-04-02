package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.Route;
import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.Waybill;
import com.nix.futuredelivery.service.DriverService;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
@RequestMapping("/driver")
public class DriverController {
    private DriverService driverService;

    public DriverController(DriverService driverService){
        this.driverService = driverService;
    }

    @ApiOperation(value = "Get all routes")
    @PreAuthorize("hasAuthority('DRIVER')")
    @GetMapping("/get_all_routes")
    public List<Route> getDriversRoute(Authentication authentication){
        SystemUser user = (SystemUser) authentication.getPrincipal();
        return driverService.getDriversRoutes(user.getId());
    }

    @ApiOperation(value = "Confirm delivered waybills")
    @PreAuthorize("hasAuthority('DRIVER')")
    @PostMapping("/set_waybills_delivered")
    public void setWaybillsDelivered(@RequestBody List<Waybill> waybills, Authentication authentication) {
        SystemUser user = (SystemUser) authentication.getPrincipal();
        driverService.checkCompletedDelivery(waybills);
    }
}
