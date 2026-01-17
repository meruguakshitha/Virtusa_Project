package com.bank.loan.service;

import com.bank.loan.dto.*;
import com.bank.loan.model.*;
import com.bank.loan.repository.LoanRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

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

    // ---------------- EXISTING TESTS ----------------

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
        assertNull(dto.getSanctionedAmount());
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

        LoanResponseDto dto = loanService.changeStatus("1", req, "user1", Role.USER);

        assertEquals(LoanStatus.SUBMITTED, dto.getStatus());
    }

    // ---------------- NEW TESTS (IMPORTANT) ----------------

    @Test
    void calculatePricing_largeLoanGetsSizeDiscount() {
        LoanCreateRequest req = sampleCreateRequest();
        req.setRequestedAmount(100_000_000L);

        double rate = loanService.calculatePricing(req);

        assertTrue(rate < 10.0);
    }

    @Test
    void calculatePricing_unknownRatingGetsHighRisk() {
        LoanCreateRequest req = sampleCreateRequest();
        req.getFinancials().setRating("CCC");

        double rate = loanService.calculatePricing(req);

        assertTrue(rate > 10.0);
    }

    @Test
    void changeStatus_adminCanMoveDraftToUnderReview() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().rating("A").build())
                .build();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.UNDER_REVIEW);

        LoanResponseDto dto = loanService.changeStatus("1", req, "admin", Role.ADMIN);

        assertEquals(LoanStatus.UNDER_REVIEW, dto.getStatus());
    }

    @Test
    void approveLoan_success() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.UNDER_REVIEW)
                .financials(Financials.builder().rating("A").build())
                .build();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        loanService.approveLoan("1", "admin1");

        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertEquals("admin1", loan.getApprovedBy());
    }

    @Test
    void approveLoan_throwsIfNotUnderReview() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .build();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class,
                () -> loanService.approveLoan("1", "admin"));
    }

    @Test
    void rejectLoan_success() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.UNDER_REVIEW)
                .build();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        loanService.rejectLoan("1", "admin");

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
        assertEquals("admin", loan.getApprovedBy());
    }

    @Test
    void rejectLoan_throwsIfNotUnderReview() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .build();

        when(loanRepository.findById("1")).thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class,
                () -> loanService.rejectLoan("1", "admin"));
    }

    @Test
    void listLoans_usesRepository() {
        Loan loan = Loan.builder()
                .id("1")
                .status(LoanStatus.DRAFT)
                .financials(Financials.builder().rating("A").build())
                .build();

        Page<Loan> page = new PageImpl<>(List.of(loan));
        when(loanRepository.findByDeletedFalse(any(Pageable.class))).thenReturn(page);

        PagedResponse<LoanResponseDto> res = loanService.listLoans(0, 10, null, null);

        assertEquals(1, res.getContent().size());
    }
}
