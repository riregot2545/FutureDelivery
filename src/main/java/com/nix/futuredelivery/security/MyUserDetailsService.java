package com.nix.futuredelivery.security;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.SystemUserRepository;
import com.nix.futuredelivery.repository.WarehouseManagerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private WarehouseManagerRepository systemUserRepository;

    public MyUserDetailsService(WarehouseManagerRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WarehouseManager user = systemUserRepository.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        return new MyUserPrincipal(user);
    }
}
