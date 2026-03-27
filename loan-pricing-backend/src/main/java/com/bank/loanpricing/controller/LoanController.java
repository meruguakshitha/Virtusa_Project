package com.bank.loanpricing.controller;

import com.bank.loanpricing.model.Loan;
import com.bank.loanpricing.model.LoanStatus;
import com.bank.loanpricing.security.UserPrincipal;
import com.bank.loanpricing.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.bank.loanpricing.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // 1️⃣ Create Loan (USER / ADMIN)
    @PostMapping
    public Loan createLoan(
            @RequestBody @Valid Loan loan,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return loanService.createLoan(loan, principal.getUserId());
    }

    // 2️⃣ Update Draft Loan (ONLY DRAFT)
    @PutMapping("/{id}")
    public Loan updateDraft(
            @PathVariable String id,
            @RequestBody @Valid Loan loan,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return loanService.updateDraft(id, loan, principal.getUserId());
    }

    // 3️⃣ Pricing Calculation
    @GetMapping("/{id}/pricing")
    public double calculatePricing(@PathVariable String id) {
        return loanService.calculatePricing(id);
    }

    // 4️⃣ Submit Loan (USER)
    @PostMapping("/{id}/submit")
    public Loan submitLoan(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return loanService.submitLoan(id, principal.getUserId());
    }

    // 5️⃣ Change Status (ADMIN)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Loan changeStatus(
            @PathVariable String id,
            @RequestParam LoanStatus status,
            @RequestParam String comments,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return loanService.changeStatus(
                id,
                status,
                principal.getUserId(),
                comments
        );
    }

    @GetMapping
    public Page<?> listLoans(Pageable pageable,
                             @AuthenticationPrincipal UserPrincipal principal) {

        boolean isAdmin = principal.getAuthorities().toString().contains("ADMIN");
        Page<Loan> loans = loanService.listLoans(pageable, isAdmin);

        return isAdmin
                ? loans.map(LoanMapper::toAdminDto)
                : loans.map(LoanMapper::toUserDto);
    }

    @GetMapping("/{id}")
    public Object getLoan(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Loan loan = loanService.getLoan(id);

        if (principal.getAuthorities().toString().contains("ADMIN")) {
            return LoanMapper.toAdminDto(loan);
        }
        return LoanMapper.toUserDto(loan);
    }

    // 6️⃣ Soft Delete (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLoan(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        loanService.softDelete(id, principal.getUserId());
    }
}
