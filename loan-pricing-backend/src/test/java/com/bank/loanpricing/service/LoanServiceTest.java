package com.bank.loanpricing.service;

import com.bank.loanpricing.model.Loan;
import com.bank.loanpricing.model.LoanStatus;
import com.bank.loanpricing.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private LoanService loanService;

    // ---------- CREATE ----------

    @Test
    void shouldCreateLoanAsDraft() {
        Loan loan = new Loan();

        when(loanRepository.save(any())).thenReturn(loan);

        Loan saved = loanService.createLoan(loan, "user1");

        assertEquals(LoanStatus.DRAFT, saved.getStatus());
        verify(loanRepository).save(loan);
    }

    // ---------- UPDATE DRAFT ----------

    @Test
    void shouldUpdateDraftLoan() {
        Loan existing = new Loan();
        existing.setStatus(LoanStatus.DRAFT);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(existing));
        when(loanRepository.save(any())).thenReturn(existing);

        Loan updated = loanService.updateDraft("1", new Loan(), "user1");

        assertNotNull(updated);
        verify(loanRepository).save(existing);
    }

    @Test
    void shouldFailUpdatingNonDraftLoan() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.SUBMITTED);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanService.updateDraft("1", new Loan(), "user1"));
    }

    // ---------- PRICING ----------

    @Test
    void shouldCalculatePricing() {
        Loan loan = new Loan();
        loan.setTenureMonths(12);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));
        when(pricingService.calculateRate(any(), anyInt()))
                .thenReturn(11.5);

        double rate = loanService.calculatePricing("1");

        assertEquals(11.5, rate);
    }

    // ---------- SUBMIT ----------

    @Test
    void shouldSubmitDraftLoan() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.DRAFT);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        Loan submitted = loanService.submitLoan("1", "user1");

        assertEquals(LoanStatus.SUBMITTED, submitted.getStatus());
    }

    @Test
    void shouldRejectInvalidSubmit() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanService.submitLoan("1", "user1"));
    }

    // ---------- CHANGE STATUS ----------

    @Test
    void shouldChangeStatusFromSubmittedToUnderReview() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.SUBMITTED);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        Loan updated = loanService.changeStatus(
                "1",
                LoanStatus.UNDER_REVIEW,
                "admin",
                "reviewing"
        );

        assertEquals(LoanStatus.UNDER_REVIEW, updated.getStatus());
    }

    @Test
    void shouldFailInvalidStatusTransition() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.DRAFT);

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanService.changeStatus(
                        "1",
                        LoanStatus.APPROVED,
                        "admin",
                        "invalid"
                ));
    }

    // ---------- DELETE ----------

    @Test
    void shouldSoftDeleteLoan() {
        Loan loan = new Loan();

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));

        loanService.softDelete("1", "admin");

        assertTrue(loan.isDeleted());
        verify(loanRepository).save(loan);
    }

    // ---------- GET ----------

    @Test
    void shouldGetLoanSuccessfully() {
        Loan loan = new Loan();

        when(loanRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(loan));

        Loan result = loanService.getLoan("1");

        assertNotNull(result);
    }

    @Test
    void shouldFailWhenLoanNotFound() {
        when(loanRepository.findByIdAndDeletedFalse("x"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loanService.getLoan("x"));
    }
}
