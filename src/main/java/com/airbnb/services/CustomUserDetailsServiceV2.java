package com.airbnb.services;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userDetailsService")
public class CustomUserDetailsServiceV2 implements UserDetailsService {

    private final IUserService userService;

    public CustomUserDetailsServiceV2(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.airbnb.entities.User loadUser = userService.getUserByEmail(username).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException("Thông tin đăng nhập không chính xác");
                });
        // TODO: Add more roles
        // List<GrantedAuthority> authorities = new ArrayList<>();
        // authorities.add((GrantedAuthority) () -> "ROLE_USER");
        // return new User(loadUser.getEmail(), loadUser.getPassword(), authorities);

        return new User(loadUser.getEmail(), loadUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
