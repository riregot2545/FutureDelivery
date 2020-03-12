package com.nix.futuredelivery.security;

import com.nix.futuredelivery.entity.SystemUser;
import com.nix.futuredelivery.entity.WarehouseManager;
import com.nix.futuredelivery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private WarehouseManagerRepository warehouseManagerRepository;
    private AdminRepository adminRepository;
    private DriverRepository driverRepository;
    private StoreManagerRepository storeManagerRepository;


    public MyUserDetailsService(WarehouseManagerRepository warehouseManagerRepository, AdminRepository adminRepository, DriverRepository driverRepository, StoreManagerRepository storeManagerRepository) {
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.adminRepository = adminRepository;
        this.driverRepository = driverRepository;
        this.storeManagerRepository = storeManagerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user;
        user = warehouseManagerRepository.findByLogin(username);
        if (user == null) {
            user = driverRepository.findByLogin(username);
            if (user == null) {
                user = storeManagerRepository.findByLogin(username);
                if (user == null) {
                    user = adminRepository.findByLogin(username);
                    if (user == null) {
                        throw new UsernameNotFoundException("User not found.");
                    }
                }

            }
        }
        return new MyUserPrincipal(user);
    }

}
