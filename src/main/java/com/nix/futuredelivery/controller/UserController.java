package com.nix.futuredelivery.controller;

import com.nix.futuredelivery.model.AuthenticationRequest;
import com.nix.futuredelivery.model.AuthenticationResponse;
import com.nix.futuredelivery.security.MyUserDetailsService;
import com.nix.futuredelivery.util.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    public UserController(AuthenticationManager authenticationManager, Jwt jwtTokenUtil, MyUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    private AuthenticationManager authenticationManager;
    private Jwt jwtTokenUtil;
    private MyUserDetailsService userDetailsService;



    @RequestMapping("/security")
    public String trySec() {
        return "success";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect name or password, e");
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getLogin());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
