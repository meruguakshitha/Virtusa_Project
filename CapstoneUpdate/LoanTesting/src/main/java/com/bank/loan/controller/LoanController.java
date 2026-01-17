package com.bank.loan.controller;

import com.bank.loan.dto.*;
import com.bank.loan.model.LoanStatus;
import com.bank.loan.model.Role;
import com.bank.loan.security.CustomUserDetails;
import com.bank.loan.service.LoanService;
import com.bank.loan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(
            @Valid @RequestBody LoanCreateRequest request,
            Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(loanService.createLoan(request, principal.getId()));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<LoanResponseDto>> listLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(required = false) Boolean my,
            Authentication authentication
    ) {
        String createdBy = null;

        if (Boolean.TRUE.equals(my) && authentication != null) {
            CustomUserDetails principal =
                    (CustomUserDetails) authentication.getPrincipal();
            createdBy = principal.getId(); // ✅ only this user's loans
        }

        return ResponseEntity.ok(
                loanService.listLoans(page, size, status, createdBy)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoan(@PathVariable String id) {
        return ResponseEntity.ok(loanService.getLoan(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponseDto> updateLoan(
            @PathVariable String id,
            @Valid @RequestBody LoanUpdateRequest request,
            Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        Role role = principal.getRole();
        return ResponseEntity.ok(
                loanService.updateLoan(id, request, principal.getId(), role));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanResponseDto> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody LoanStatusUpdateRequest request,
            Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        Role role = principal.getRole();
        return ResponseEntity.ok(
                loanService.changeStatus(id, request, principal.getId(), role));
    }
}
