package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.model.AuthenticationRequest;
import com.nix.futuredelivery.model.AuthenticationResponse;
import com.nix.futuredelivery.security.MyUserDetailsService;

import com.nix.futuredelivery.service.WarehouseManagerService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("/security")
    public String trySec() {
        return "success";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        return null;
        //return userService.createAuthenticationToken(authenticationRequest);
    }

    @PostMapping("/registration")
    public void registrateUser(@RequestBody SystemUser user){

    }
}
