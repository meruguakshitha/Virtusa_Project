package com.bank.loan.service;

import com.bank.loan.dto.*;
import com.bank.loan.model.*;
import com.bank.loan.repository.LoanRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private LoanCreateRequest sampleCreateRequest() {
        LoanCreateRequest req = new LoanCreateRequest();
        req.setClientName("Client");
        req.setLoanType("TermLoan");
        req.setRequestedAmount(10_000_000L);
        req.setProposedInterestRate(0.0);
        req.setTenureMonths(12);
        FinancialsDto fin = new FinancialsDto();
        fin.setRevenue(100_000_000L);
        fin.setEbitda(10_000_000L);
        fin.setRating("A");
        req.setFinancials(fin);
        return req;
    }

    @Test
    void createLoan_setsDraftAndSuggestedRate() {
        LoanCreateRequest req = sampleCreateRequest();

        when(loanRepository.save(any())).thenAnswer(invocation -> {
            Loan l = invocation.getArgument(0);
            l.setId("1");
            return l;
        });

        LoanResponseDto dto = loanService.createLoan(req, "user1");

        assertEquals(LoanStatus.DRAFT, dto.getStatus());
        assertNotNull(dto.getProposedInterestRate());
        verify(loanRepository).save(any());
    }

    @Test
    void getLoan_throwsWhenDeleted() {
        Loan loan = Loan.builder().id("1").deleted(true).build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class, () -> loanService.getLoan("1"));
    }

    @Test
    void updateLoan_userCanUpdateDraftOnly() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoanUpdateRequest req = new LoanUpdateRequest();
        req.setClientName("NewClient");
        req.setLoanType("TermLoan");
        req.setRequestedAmount(20_000_000L);
        req.setProposedInterestRate(12.5);
        req.setTenureMonths(24);
        FinancialsDto fin = new FinancialsDto();
        fin.setRevenue(200_000_000L);
        fin.setEbitda(20_000_000L);
        fin.setRating("BBB");
        req.setFinancials(fin);

        LoanResponseDto dto = loanService.updateLoan("1", req, "user1", Role.USER);

        assertEquals("NewClient", dto.getClientName());
        assertNull(dto.getSanctionedAmount()); // user cannot set sensitive fields
    }

    @Test
    void updateLoan_userCannotUpdateNonDraft() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.SUBMITTED)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        LoanUpdateRequest req = new LoanUpdateRequest();
        req.setClientName("New");
        req.setLoanType("TermLoan");
        req.setRequestedAmount(1L);
        req.setProposedInterestRate(1.0);
        req.setTenureMonths(1);
        FinancialsDto fin = new FinancialsDto();
        fin.setRevenue(1L);
        fin.setEbitda(1L);
        fin.setRating("A");
        req.setFinancials(fin);

        assertThrows(IllegalStateException.class,
                () -> loanService.updateLoan("1", req, "user1", Role.USER));
    }

    @Test
    void changeStatus_userDraftToSubmitted() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.SUBMITTED);
        req.setComments("Submit");

        LoanResponseDto dto = loanService.changeStatus("1", req, "user1", Role.USER);

        assertEquals(LoanStatus.SUBMITTED, dto.getStatus());
    }

    @Test
    void changeStatus_adminSubmittedToUnderReview() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.SUBMITTED)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.UNDER_REVIEW);

        LoanResponseDto dto = loanService.changeStatus("1", req, "admin1", Role.ADMIN);

        assertEquals(LoanStatus.UNDER_REVIEW, dto.getStatus());
    }

    @Test
    void changeStatus_adminUnderReviewToApproved_setsApprovedBy() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.UNDER_REVIEW)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.APPROVED);

        LoanResponseDto dto = loanService.changeStatus("1", req, "admin1", Role.ADMIN);

        assertEquals(LoanStatus.APPROVED, dto.getStatus());
        assertEquals("admin1", dto.getApprovedBy());
    }

    @Test
    void changeStatus_invalidTransitionThrows() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.APPROVED);

        assertThrows(IllegalStateException.class,
                () -> loanService.changeStatus("1", req, "admin1", Role.ADMIN));
    }

    @Test
    void softDeleteLoan_setsDeletedFlag() {
        Loan loan = Loan.builder()
                .id("1")
                .deleted(false)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        loanService.softDeleteLoan("1", "admin1");

        assertTrue(loan.isDeleted());
        verify(loanRepository).save(any());
    }

    @Test
    void listLoans_usesRepository() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().revenue(1L).ebitda(1L).rating("A").build())
                .build();
        Page<Loan> page = new PageImpl<>(List.of(loan));
        when(loanRepository.findByDeletedFalse(any(Pageable.class))).thenReturn(page);

        PagedResponse<LoanResponseDto> res = loanService.listLoans(0, 10, null, null);

        assertEquals(1, res.getContent().size());
        assertEquals(1, res.getTotalElements());
    }
}
