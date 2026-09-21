package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.CreateGymWithAdminRequest;
import com.gms.gmsmvp.dto.CreateGymWithAdminResponse;
import com.gms.gmsmvp.service.SuperAdminGymService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/superadmin/gyms")
@RequiredArgsConstructor
public class SuperAdminGymController {

    private final SuperAdminGymService superAdminGymService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateGymWithAdminResponse create(@Valid @RequestBody CreateGymWithAdminRequest request) {
        return superAdminGymService.createGymWithAdmin(request);
    }
}
