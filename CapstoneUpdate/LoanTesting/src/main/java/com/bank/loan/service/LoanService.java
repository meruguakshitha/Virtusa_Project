package com.bank.loan.service;

import com.bank.loan.dto.*;
import com.bank.loan.model.*;
import com.bank.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    // ==================================================
    // PRICING
    // ==================================================
    private double calculatePricing(Loan loan) {

        double baseRate = 10.0;

        double ratingFactor = switch (loan.getFinancials().getRating()) {
            case "AAA", "AA" -> -1.0;
            case "A" -> 0.0;
            case "BBB" -> 1.0;
            default -> 2.0;
        };

        double sizeFactor =
                loan.getRequestedAmount() > 50_000_000L ? -0.5 : 0.0;

        return baseRate + ratingFactor + sizeFactor;
    }

    // ==================================================
    // SINGLE SOURCE OF TRUTH VALIDATION (IMPORTANT)
    // ==================================================
    private void validateForSubmit(Loan loan) {

        if (!StringUtils.hasText(loan.getClientName()))
            throw new IllegalStateException("Client name required");

        if (!StringUtils.hasText(loan.getLoanType()))
            throw new IllegalStateException("Loan type required");

        if (loan.getRequestedAmount() == null || loan.getRequestedAmount() <= 0)
            throw new IllegalStateException("Requested amount required");

        if (loan.getTenureMonths() == null || loan.getTenureMonths() <= 0)
            throw new IllegalStateException("Tenure required");

        if (loan.getFinancials() == null)
            throw new IllegalStateException("Financials required");

        if (loan.getFinancials().getRevenue() == null)
            throw new IllegalStateException("Revenue required");

        if (loan.getFinancials().getEbitda() == null)
            throw new IllegalStateException("EBITDA required");

        if (!StringUtils.hasText(loan.getFinancials().getRating()))
            throw new IllegalStateException("Rating required");
    }

    // ==================================================
    // CREATE LOAN (SAVE DRAFT / SUBMIT)
    // ==================================================
    public LoanResponseDto createLoan(LoanCreateRequest request, String userId) {

        Loan loan = Loan.builder()
                .clientName(request.getClientName())
                .loanType(request.getLoanType())
                .requestedAmount(request.getRequestedAmount())
                .tenureMonths(request.getTenureMonths())
                .financials(
                        request.getFinancials() != null
                                ? Financials.builder()
                                .revenue(request.getFinancials().getRevenue())
                                .ebitda(request.getFinancials().getEbitda())
                                .rating(request.getFinancials().getRating())
                                .build()
                                : null
                )
                .createdBy(userId)
                .updatedBy(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deleted(false)
                .build();

        if (request.getAction() == LoanCreateAction.SUBMIT) {

            validateForSubmit(loan);

            loan.setStatus(LoanStatus.SUBMITTED);
            loan.setProposedInterestRate(calculatePricing(loan));

        } else {
            loan.setStatus(LoanStatus.DRAFT);
        }

        loan.getActions().add(
                LoanAction.builder()
                        .by(userId)
                        .action(
                                loan.getStatus() == LoanStatus.DRAFT
                                        ? "SAVED_DRAFT"
                                        : "SUBMITTED"
                        )
                        .timestamp(Instant.now())
                        .build()
        );

        return toDto(loanRepository.save(loan));
    }

    // ==================================================
    // UPDATE LOAN (ONLY DRAFT BY USER)
    // ==================================================
    public LoanResponseDto updateLoan(String id,
                                      LoanUpdateRequest request,
                                      String userId,
                                      Role role) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted())
            throw new IllegalStateException("Loan is deleted");

        if (role == Role.USER && loan.getStatus() != LoanStatus.DRAFT)
            throw new IllegalStateException("USER can edit only DRAFT loans");

        loan.setClientName(request.getClientName());
        loan.setLoanType(request.getLoanType());
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setTenureMonths(request.getTenureMonths());

        loan.setFinancials(
                Financials.builder()
                        .revenue(request.getFinancials().getRevenue())
                        .ebitda(request.getFinancials().getEbitda())
                        .rating(request.getFinancials().getRating())
                        .build()
        );

        if (role == Role.ADMIN) {
            loan.setSanctionedAmount(request.getSanctionedAmount());
            loan.setApprovedInterestRate(request.getApprovedInterestRate());
        }

        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        loan.getActions().add(
                LoanAction.builder()
                        .by(userId)
                        .action("UPDATED")
                        .timestamp(Instant.now())
                        .build()
        );

        return toDto(loanRepository.save(loan));
    }

    // ==================================================
    // CHANGE STATUS (SUBMIT / APPROVE / REJECT)
    // ==================================================
    public LoanResponseDto changeStatus(String id,
                                        LoanStatusUpdateRequest request,
                                        String userId,
                                        Role role) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        LoanStatus from = loan.getStatus();
        LoanStatus to = request.getStatus();

        if (role == Role.USER) {

            if (from == LoanStatus.DRAFT && to == LoanStatus.SUBMITTED) {
                validateForSubmit(loan);
            } else {
                throw new IllegalStateException("Invalid USER status transition");
            }
        }

        if (role == Role.ADMIN) {

            if (from == LoanStatus.UNDER_REVIEW &&
                    (to == LoanStatus.APPROVED || to == LoanStatus.REJECTED)) {

                loan.setApprovedBy(userId);
                loan.setApprovedAt(Instant.now());

            } else if (to != LoanStatus.UNDER_REVIEW) {
                throw new IllegalStateException("Invalid ADMIN status transition");
            }
        }

        loan.setStatus(to);
        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        loan.getActions().add(
                LoanAction.builder()
                        .by(userId)
                        .action("STATUS_" + to.name())
                        .comments(request.getComments())
                        .timestamp(Instant.now())
                        .build()
        );

        return toDto(loanRepository.save(loan));
    }

    // ==================================================
    // GET + LIST
    // ==================================================
    public LoanResponseDto getLoan(String id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted())
            throw new IllegalStateException("Loan is deleted");

        return toDto(loan);
    }

    public PagedResponse<LoanResponseDto> listLoans(int page,
                                                    int size,
                                                    LoanStatus status,
                                                    String createdBy) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Loan> loans;

        if (createdBy != null) {
            loans = loanRepository.findByDeletedFalseAndCreatedBy(createdBy, pageable);
        } else if (status != null) {
            loans = loanRepository.findByDeletedFalseAndStatus(status, pageable);
        } else {
            loans = loanRepository.findByDeletedFalse(pageable);
        }

        return PagedResponse.<LoanResponseDto>builder()
                .content(loans.getContent().stream().map(this::toDto).toList())
                .page(loans.getNumber())
                .size(loans.getSize())
                .totalElements(loans.getTotalElements())
                .totalPages(loans.getTotalPages())
                .build();
    }

    // ==================================================
    // DTO MAPPER
    // ==================================================
    private LoanResponseDto toDto(Loan loan) {

        FinancialsDto finDto = loan.getFinancials() == null ? null :
                FinancialsDto.builder()
                        .revenue(loan.getFinancials().getRevenue())
                        .ebitda(loan.getFinancials().getEbitda())
                        .rating(loan.getFinancials().getRating())
                        .build();

        return LoanResponseDto.builder()
                .id(loan.getId())
                .clientName(loan.getClientName())
                .loanType(loan.getLoanType())
                .requestedAmount(loan.getRequestedAmount())
                .proposedInterestRate(loan.getProposedInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .financials(finDto)
                .status(loan.getStatus())
                .createdBy(loan.getCreatedBy())
                .approvedBy(loan.getApprovedBy())
                .approvedAt(loan.getApprovedAt())
                .deleted(loan.isDeleted())
                .build();
    }


    @Transactional
    public void softDeleteLoan(String id, String userId) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted()) {
            return;
        }

        loan.setDeleted(true);
        loan.setDeletedAt(Instant.now());
        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        loan.getActions().add(
                LoanAction.builder()
                        .by(userId)
                        .action("DELETED")
                        .timestamp(Instant.now())
                        .build()
        );

        loanRepository.save(loan);
    }

}
