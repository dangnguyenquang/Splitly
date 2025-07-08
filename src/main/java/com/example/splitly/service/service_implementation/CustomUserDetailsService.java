package com.example.splitly.service.service_implementation;

import com.example.splitly.entity.User;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userResponsitory;

    public CustomUserDetailsService(UserRepository userResponsitory) {
        this.userResponsitory = userResponsitory;
    }

    public CustomUserDetailsService() {
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userResponsitory.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
