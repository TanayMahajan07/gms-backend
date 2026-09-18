package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.LoginRequest;
import com.gms.gmsmvp.dto.LoginResponse;
import com.gms.gmsmvp.dto.SignupRequest;
import com.gms.gmsmvp.dto.SignupResponse;
import com.gms.gmsmvp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/signup")
    public SignupResponse signup(
            @Valid @RequestBody SignupRequest request) {

        return authService.signup(request);
    }

}
