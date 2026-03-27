package com.bank.loanpricing.service;

import com.bank.loanpricing.model.Loan;
import com.bank.loanpricing.model.LoanAction;
import com.bank.loanpricing.model.LoanStatus;
import com.bank.loanpricing.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final com.bank.loanpricing.service.PricingService pricingService;

    public LoanService(
            LoanRepository loanRepository,
            com.bank.loanpricing.service.PricingService pricingService
    ) {
        this.loanRepository = loanRepository;
        this.pricingService = pricingService;
    }

    // 1️⃣ Create Loan (DRAFT)
    public Loan createLoan(Loan loan, String userId) {

        loan.setStatus(LoanStatus.DRAFT);
        loan.setCreatedBy(userId);
        loan.setCreatedAt(Instant.now());

        addAction(loan, userId, "DRAFT_CREATED", "Loan created");

        return loanRepository.save(loan);
    }

    // 2️⃣ Update Loan (ONLY DRAFT)
    public Loan updateDraft(String loanId, Loan updatedLoan, String userId) {

        Loan loan = getActiveLoan(loanId);

        if (loan.getStatus() != LoanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT loans can be edited");
        }

        loan.setClientName(updatedLoan.getClientName());
        loan.setLoanType(updatedLoan.getLoanType());
        loan.setRequestedAmount(updatedLoan.getRequestedAmount());
        loan.setProposedInterestRate(updatedLoan.getProposedInterestRate());
        loan.setTenureMonths(updatedLoan.getTenureMonths());
        loan.setFinancials(updatedLoan.getFinancials());

        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        addAction(loan, userId, "DRAFT_UPDATED", "Draft updated");

        return loanRepository.save(loan);
    }

    // 3️⃣ Pricing Calculation
    public double calculatePricing(String loanId) {

        Loan loan = getActiveLoan(loanId);

        return pricingService.calculateRate(
                loan.getFinancials(),
                loan.getTenureMonths()
        );
    }

    // 4️⃣ Submit Loan
    public Loan submitLoan(String loanId, String userId) {

        Loan loan = getActiveLoan(loanId);

        if (loan.getStatus() != LoanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT loans can be submitted");
        }

        loan.setStatus(LoanStatus.SUBMITTED);
        loan.setUpdatedBy(userId);
        loan.setUpdatedAt(Instant.now());

        addAction(loan, userId, "SUBMITTED", "Loan submitted");

        return loanRepository.save(loan);
    }

    // 5️⃣ Change Status (ADMIN)
    public Loan changeStatus(
            String loanId,
            LoanStatus newStatus,
            String adminId,
            String comments
    ) {

        Loan loan = getActiveLoan(loanId);

        boolean validTransition =
                (loan.getStatus() == LoanStatus.SUBMITTED && newStatus == LoanStatus.UNDER_REVIEW)
                        || (loan.getStatus() == LoanStatus.UNDER_REVIEW &&
                        (newStatus == LoanStatus.APPROVED || newStatus == LoanStatus.REJECTED));


        if (!validTransition) {
            throw new IllegalStateException("Invalid status transition");
        }

        loan.setStatus(newStatus);
        loan.setApprovedBy(adminId);
        loan.setApprovedAt(Instant.now());

        addAction(loan, adminId, newStatus.name(), comments);

        return loanRepository.save(loan);
    }

    // 6️⃣ Soft Delete (ADMIN)
    public void softDelete(String loanId, String adminId) {

        Loan loan = getActiveLoan(loanId);

        loan.setDeleted(true);
        loan.setDeletedAt(Instant.now());

        addAction(loan, adminId, "DELETED", "Loan soft deleted");

        loanRepository.save(loan);
    }

    public Page<Loan> listLoans(Pageable pageable, boolean isAdmin) {
        return loanRepository.findByDeletedFalse(pageable);
    }

    public Loan getLoan(String id) {
        return getActiveLoan(id);
    }

    // 🔒 Helper Methods
    private Loan getActiveLoan(String loanId) {
        return loanRepository.findByIdAndDeletedFalse(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    private void addAction(
            Loan loan,
            String userId,
            String action,
            String comments
    ) {
        loan.getActions().add(
                new LoanAction(userId, action, comments, Instant.now())
        );
    }
}
