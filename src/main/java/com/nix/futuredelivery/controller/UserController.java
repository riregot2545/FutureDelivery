package com.nix.futuredelivery.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UserController {

    @RequestMapping("/security")
    public String trySec(){
        return "success";
    }
}
