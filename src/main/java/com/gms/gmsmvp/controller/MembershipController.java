package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.MembershipCreateRequest;
import com.gms.gmsmvp.dto.MembershipRenewRequest;
import com.gms.gmsmvp.dto.MembershipResponse;
import com.gms.gmsmvp.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipResponse create(@Valid @RequestBody MembershipCreateRequest request) {
        return membershipService.create(request);
    }

    @PostMapping("/{id}/renew")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipResponse renew(@PathVariable Long id,
                                    @Valid @RequestBody MembershipRenewRequest request) {
        return membershipService.renew(id, request);
    }

    @GetMapping
    public Page<MembershipResponse> search(@RequestParam(required = false) String search,
                                           @RequestParam(required = false) String status,
                                           @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return membershipService.search(search, status, pageable);
    }

    @GetMapping("/{id}")
    public MembershipResponse getById(@PathVariable Long id) {
        return membershipService.getById(id);
    }
}
