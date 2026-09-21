package com.gms.gmsmvp.service;

import com.gms.gmsmvp.repository.RolePermissionMappingRepository;
import com.gms.gmsmvp.repository.UserRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionMappingRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {



        var user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Users not found: " + username
                        ));

        List<String> permissions =
                rolePermissionRepository
                        .findPermissionCodesByRoleId(
                                user.getRole().getId()
                        );

        return new GmsUserDetails(
                user,
                permissions
        );
    }
}
