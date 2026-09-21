package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.MembershipPaymentSummaryResponse;
import com.gms.gmsmvp.dto.PaymentCreateRequest;
import com.gms.gmsmvp.dto.PaymentResponse;
import com.gms.gmsmvp.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.create(request);
    }

    @GetMapping
    public Page<PaymentResponse> search(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) String status,
                                        @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return paymentService.search(search, status, pageable);
    }

    @GetMapping("/membership/{membershipId}/summary")
    public MembershipPaymentSummaryResponse summary(@PathVariable Long membershipId) {
        return paymentService.summaryForMembership(membershipId);
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }
}
