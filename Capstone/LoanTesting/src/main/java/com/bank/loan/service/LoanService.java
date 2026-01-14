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

    /**
     * Simple pricing calculation: baseRate + riskPremium based on rating.
     */
    public double calculatePricing(LoanCreateRequest req) {
        double baseRate = 10.0;
        double ratingFactor = switch (req.getFinancials().getRating()) {
            case "AAA", "AA" -> -1.0;
            case "A" -> 0.0;
            case "BBB" -> 1.0;
            default -> 2.0;
        };
        double sizeFactor = req.getRequestedAmount() > 50_000_000L ? -0.5 : 0.0;
        return baseRate + ratingFactor + sizeFactor;
    }

    public LoanResponseDto createLoan(LoanCreateRequest request, String userId) {
        double suggestedRate = calculatePricing(request);

        var financials = Financials.builder()
                .revenue(request.getFinancials().getRevenue())
                .ebitda(request.getFinancials().getEbitda())
                .rating(request.getFinancials().getRating())
                .build();

        var loan = Loan.builder()
                .clientName(request.getClientName())
                .loanType(request.getLoanType())
                .requestedAmount(request.getRequestedAmount())
                .proposedInterestRate(suggestedRate)
                .tenureMonths(request.getTenureMonths())
                .financials(financials)
                .status(LoanStatus.DRAFT)
                .createdBy(userId)
                .updatedBy(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .deleted(false)
                .build();

        loan.getActions().add(LoanAction.builder()
                .by(userId)
                .action("CREATED")
                .comments("Loan created in DRAFT")
                .timestamp(Instant.now())
                .build());

        return toDto(loanRepository.save(loan));
    }

    public LoanResponseDto updateLoan(String id, LoanUpdateRequest request,
                                      String userId, Role role) {

        var loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted()) {
            throw new IllegalStateException("Loan is deleted");
        }

        if (role == Role.USER && loan.getStatus() != LoanStatus.DRAFT) {
            throw new IllegalStateException("USER can edit only DRAFT loans");
        }

        loan.setClientName(request.getClientName());
        loan.setLoanType(request.getLoanType());
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setProposedInterestRate(request.getProposedInterestRate());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setFinancials(Financials.builder()
                .revenue(request.getFinancials().getRevenue())
                .ebitda(request.getFinancials().getEbitda())
                .rating(request.getFinancials().getRating())
                .build());

        if (role == Role.ADMIN) {
            if (request.getSanctionedAmount() != null) {
                loan.setSanctionedAmount(request.getSanctionedAmount());
            }
            if (request.getApprovedInterestRate() != null) {
                loan.setApprovedInterestRate(request.getApprovedInterestRate());
            }
        }

        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        loan.getActions().add(LoanAction.builder()
                .by(userId)
                .action("UPDATED")
                .comments("Fields updated")
                .timestamp(Instant.now())
                .build());

        return toDto(loanRepository.save(loan));
    }

    public LoanResponseDto changeStatus(String id,
                                        LoanStatusUpdateRequest request,
                                        String userId,
                                        Role role) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted()) {
            throw new IllegalStateException("Loan is deleted");
        }

        LoanStatus from = loan.getStatus();
        LoanStatus to = request.getStatus();

        // USER RULES
        if (role == Role.USER) {
            if (from == LoanStatus.DRAFT && to == LoanStatus.SUBMITTED) {
                // OK
            } else {
                throw new IllegalStateException(
                        "USER can only submit DRAFT → SUBMITTED"
                );
            }
        }

        // ADMIN RULES (🔥 FIXED)
        else if (role == Role.ADMIN) {

            // ✅ ADMIN CAN START REVIEW DIRECTLY
            if (
                    (from == LoanStatus.DRAFT && to == LoanStatus.UNDER_REVIEW) ||
                            (from == LoanStatus.SUBMITTED && to == LoanStatus.UNDER_REVIEW) ||
                            (from == LoanStatus.UNDER_REVIEW &&
                                    (to == LoanStatus.APPROVED || to == LoanStatus.REJECTED))
            ) {

                if (to == LoanStatus.APPROVED || to == LoanStatus.REJECTED) {
                    loan.setApprovedBy(userId);
                    loan.setApprovedAt(Instant.now());
                }

            } else {
                throw new IllegalStateException(
                        "Invalid status transition for ADMIN"
                );
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




    public void softDeleteLoan(String id, String userId) {
        var loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted()) return;

        loan.setDeleted(true);
        loan.setDeletedAt(Instant.now());
        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        loan.getActions().add(LoanAction.builder()
                .by(userId)
                .action("DELETED")
                .timestamp(Instant.now())
                .build());

        loanRepository.save(loan);
    }

    public LoanResponseDto getLoan(String id) {
        var loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.isDeleted()) {
            throw new IllegalStateException("Loan is deleted");
        }
        return toDto(loan);
    }

    private LoanResponseDto toDto(Loan loan) {

        FinancialsDto finDto = null;
        if (loan.getFinancials() != null) {
            finDto = new FinancialsDto();
            finDto.setRevenue(loan.getFinancials().getRevenue());
            finDto.setEbitda(loan.getFinancials().getEbitda());
            finDto.setRating(loan.getFinancials().getRating());
        }


        List<LoanActionDto> actionDtos =
                loan.getActions() != null
                        ? loan.getActions().stream().map(a ->
                        LoanActionDto.builder()
                                .by(a.getBy())
                                .action(a.getAction())
                                .comments(a.getComments())
                                .timestamp(a.getTimestamp())
                                .build()
                ).toList()
                        : List.of();

        return LoanResponseDto.builder()
                .id(loan.getId())
                .clientName(loan.getClientName())
                .loanType(loan.getLoanType())
                .requestedAmount(loan.getRequestedAmount())
                .proposedInterestRate(loan.getProposedInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .financials(finDto)
                .status(loan.getStatus())
                .sanctionedAmount(loan.getSanctionedAmount())
                .approvedInterestRate(loan.getApprovedInterestRate())
                .createdBy(loan.getCreatedBy())
                .updatedBy(loan.getUpdatedBy())
                .approvedBy(loan.getApprovedBy())
                .approvedAt(loan.getApprovedAt())
                .actions(actionDtos)
                .deleted(loan.isDeleted())
                .build();
    }

    public PagedResponse<LoanResponseDto> listLoans(int page, int size,
                                                    LoanStatus status,
                                                    String createdBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
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

    // 🔥 FIXED METHODS (ONLY CHANGE)



    @Transactional
    public void approveLoan(String loanId, String adminId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new RuntimeException("Only UNDER_REVIEW loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedBy(adminId);
        loan.setApprovedAt(Instant.now());

        loan.getActions().add(LoanAction.builder()
                .by(adminId)
                .action("STATUS_APPROVED")
                .comments("Approved by admin")
                .timestamp(Instant.now())
                .build());

        loanRepository.save(loan);
    }

    @Transactional
    public void rejectLoan(String loanId, String adminId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new RuntimeException("Only UNDER_REVIEW loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setApprovedBy(adminId);
        loan.setApprovedAt(Instant.now());

        loan.getActions().add(LoanAction.builder()
                .by(adminId)
                .action("STATUS_REJECTED")
                .comments("Rejected by admin")
                .timestamp(Instant.now())
                .build());

        loanRepository.save(loan);
    }

}
