package com.gms.gmsmvp.service;

import com.gms.gmsmvp.dto.LoginRequest;
import com.gms.gmsmvp.dto.LoginResponse;
import com.gms.gmsmvp.dto.SignupRequest;
import com.gms.gmsmvp.dto.SignupResponse;
import com.gms.gmsmvp.entity.Role;
import com.gms.gmsmvp.entity.Users;
import com.gms.gmsmvp.repository.RoleRepository;
import com.gms.gmsmvp.repository.UserRepository;
import com.gms.gmsmvp.security.GmsUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        GmsUserDetails principal =
                (GmsUserDetails) authentication.getPrincipal();

        Users users = userRepository
                .findByUsername(principal.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Users not found"));

        users.setLastLoginAt(LocalDateTime.now());

        userRepository.save(users);

        String token = jwtService.generateToken(
                principal,
                Map.of(
                        "userId", principal.getId(),
                        "role", principal.getRoleName(),
                        "gymId", principal.getGymId() == null
                                ? ""
                                : principal.getGymId()
                )
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                principal.getId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getRoleName(),
                principal.getGymId()
        );
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }

        Role staffRole = roleRepository
                .findByRoleName("STAFF")
                .orElseThrow(() ->
                        new RuntimeException("STAFF role not found"));

        Users user = new Users();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Never store plain password
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobile(request.getMobile());

        // Default role
        user.setRole(staffRole);

        // Default gym
        user.setGymId(1L);

        // New user is active
        user.setActive(true);

        Users savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                buildFullName(savedUser),
                savedUser.getRole().getRoleName()
        );
    }

    private String buildFullName(Users user) {

        if (user.getLastName() == null ||
                user.getLastName().isBlank()) {

            return user.getFirstName();
        }

        return user.getFirstName()
                + " "
                + user.getLastName();
    }
}
