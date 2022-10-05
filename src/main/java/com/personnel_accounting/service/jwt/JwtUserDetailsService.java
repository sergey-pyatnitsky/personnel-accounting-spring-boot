package com.personnel_accounting.service.jwt;

import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.find(username);
        if (user == null) throw new UsernameNotFoundException("User not found with username: " + username);
        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword().replace("{bcrypt}", ""), user.isActive(),
                true, true, true,
                userService.getAuthoritiesByUsername(username).stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .collect(Collectors.toList()));
    }
}
