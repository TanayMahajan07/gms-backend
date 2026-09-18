package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.MemberRequest;
import com.gms.gmsmvp.dto.MemberResponse;
import com.gms.gmsmvp.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @GetMapping
    public Page<MemberResponse> search(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) Long gymId,
                                       @RequestParam(required = false) Boolean active,
                                       @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return memberService.search(search, gymId, active, pageable);
    }

    @GetMapping("/{id}")
    public MemberResponse getById(@PathVariable Long id) {
        return memberService.getById(id);
    }

    @GetMapping("/code/{memberCode}")
    public MemberResponse getByCode(@PathVariable String memberCode) {
        return memberService.getByCode(memberCode);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public MemberResponse updateStatus(@PathVariable Long id,
                                       @RequestParam boolean active,
                                       @RequestParam(required = false) Long updatedBy) {
        return memberService.updateActive(id, active, updatedBy);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id, @RequestParam(required = false) Long updatedBy) {
        memberService.deactivate(id, updatedBy);
    }
}
