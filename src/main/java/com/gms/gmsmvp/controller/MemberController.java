package com.gms.gmsmvp.controller;

import com.gms.gmsmvp.dto.MemberRequest;
import com.gms.gmsmvp.dto.MemberResponse;
import com.gms.gmsmvp.response.ApiSuccessResponse;
import com.gms.gmsmvp.service.MemberService;
import com.gms.gmsmvp.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiSuccessResponse<MemberResponse>> create(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, "Member created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse< Page<MemberResponse>>> search(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) Boolean active,
                                       @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<MemberResponse> response =memberService.search(search , active, pageable);

        return ResponseEntity.ok(ApiResponseUtil.success("Members found successfully", response));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ApiSuccessResponse<MemberResponse>> getById(@PathVariable Long id) {

        MemberResponse response = memberService.getById(id);
        return ResponseEntity.ok(ApiResponseUtil.success("Member found successfully", response));
    }

    @GetMapping("/code/{memberCode}")
    public  ResponseEntity<ApiSuccessResponse<MemberResponse>> getByCode(@PathVariable String memberCode) {
        MemberResponse response = memberService.getByCode(memberCode);
        return ResponseEntity.ok(ApiResponseUtil.success("Member found successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<MemberResponse>> update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.update(id, request);
        return ResponseEntity.ok(ApiResponseUtil.success("Member updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiSuccessResponse<MemberResponse>> updateStatus(@PathVariable Long id,
                                                                           @RequestParam boolean active,
                                                                           @RequestParam(required = false) Long updatedBy) {
        MemberResponse response = memberService.updateActive(id, active);
        return ResponseEntity.ok(ApiResponseUtil.success("Member status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResponse<Void>> deactivate(@PathVariable Long id, @RequestParam(required = false) Long updatedBy) {
        memberService.deactivate(id, updatedBy);
        return ResponseEntity.ok(ApiResponseUtil.success("Member deactivated successfully", null));
    }
}
