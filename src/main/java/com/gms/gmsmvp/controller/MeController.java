package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.GymResponse;
import com.gms.gmsmvp.dto.GymUpdateRequest;
import com.gms.gmsmvp.dto.ProfileResponse;
import com.gms.gmsmvp.dto.ProfileUpdateRequest;
import com.gms.gmsmvp.service.MeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    @GetMapping("/profile")
    public ProfileResponse getProfile() {
        return meService.getProfile();
    }

    @PutMapping("/profile")
    public ProfileResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return meService.updateProfile(request);
    }

    @GetMapping("/gym")
    public GymResponse getGym() {
        return meService.getMyGym();
    }

    @PutMapping("/gym")
    public GymResponse updateGym(@Valid @RequestBody GymUpdateRequest request) {
        return meService.updateMyGym(request);
    }
}
