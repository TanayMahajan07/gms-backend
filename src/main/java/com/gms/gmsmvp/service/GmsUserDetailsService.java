package com.gms.gmsmvp.service;

import com.gms.gmsmvp.repository.UserRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(GmsUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found: " + username));
    }
}
