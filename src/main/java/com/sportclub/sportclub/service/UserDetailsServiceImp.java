package com.sportclub.sportclub.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.sportclub.sportclub.entities.UserApp;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {

    AdminService adminService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user=adminService.loadUserByUsername(username);
        if (user==null) throw new UsernameNotFoundException(String.format("User %s not found",username));
        SimpleGrantedAuthority authority=new SimpleGrantedAuthority(user.getRoles().getRoleName());

        return User.withUsername(user.getEmail())
                .password(user.getPassword())

                .authorities(authority)
//                .roles(String.valueOf(user.getRoles()))
                .build();
    }
}
