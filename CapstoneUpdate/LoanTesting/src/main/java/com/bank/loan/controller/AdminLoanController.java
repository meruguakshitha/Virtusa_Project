package com.bank.loan.controller;

import com.bank.loan.dto.LoanStatusUpdateRequest;
import com.bank.loan.model.LoanStatus;
import com.bank.loan.model.Role;
import com.bank.loan.security.CustomUserDetails;
import com.bank.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/*
@RestController
@RequestMapping("/api/admin/loans")
@RequiredArgsConstructor
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminLoanController {

    private final LoanService loanService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable String id,
                                           Authentication authentication) {
        var principal = (CustomUserDetails) authentication.getPrincipal();
        loanService.softDeleteLoan(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable String id,
                                        Authentication authentication) {
        var admin = (CustomUserDetails) authentication.getPrincipal();
        loanService.approveLoan(id, admin.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable String id,
                                       Authentication authentication) {
        var admin = (CustomUserDetails) authentication.getPrincipal();
        loanService.rejectLoan(id, admin.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LoanStatus status,
            Authentication authentication) {

        var admin = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(
                loanService.listLoans(page, size, status, null)
        );
    }


}*/



@RestController
@RequestMapping("/api/admin/loans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<?> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LoanStatus status) {

        return ResponseEntity.ok(
                loanService.listLoans(page, size, status, null)
        );
    }

    // ✅ SINGLE STATUS CHANGE ENDPOINT (DOCUMENT)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable String id,
            @RequestBody LoanStatusUpdateRequest request,
            Authentication authentication) {

        var admin = (CustomUserDetails) authentication.getPrincipal();

        loanService.changeStatus(
                id,
                request,
                admin.getId(),
                Role.ADMIN
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(
            @PathVariable String id,
            Authentication authentication) {

        var admin = (CustomUserDetails) authentication.getPrincipal();
        loanService.softDeleteLoan(id, admin.getId());

        return ResponseEntity.noContent().build();
    }
}
