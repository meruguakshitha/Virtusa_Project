package com.bank.loan.controller;

import com.bank.loan.dto.*;
import com.bank.loan.exception.GlobalExceptionHandler;
import com.bank.loan.model.LoanStatus;
import com.bank.loan.model.Role;
import com.bank.loan.security.CustomUserDetails;
import com.bank.loan.service.LoanService;
import com.bank.loan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoanController loanController;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(loanController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

    }

    private CustomUserDetails samplePrincipal() {
        com.bank.loan.model.User user = com.bank.loan.model.User.builder()
                .id("user1")
                .email("rm@bank.com")
                .password("ENC")
                .role(Role.USER)
                .active(true)
                .build();
        return new CustomUserDetails(user);
    }

    @Test
    void createLoan_callsService() throws Exception {
        LoanCreateRequest req = new LoanCreateRequest();
        req.setClientName("Client");
        req.setLoanType("TermLoan");
        req.setRequestedAmount(10_000L);
        req.setProposedInterestRate(0.0);
        req.setTenureMonths(12);
        FinancialsDto fin = new FinancialsDto();
        fin.setRevenue(1L);
        fin.setEbitda(1L);
        fin.setRating("A");
        req.setFinancials(fin);

        LoanResponseDto dto = LoanResponseDto.builder()
                .id("1")
                .clientName("Client")
                .status(LoanStatus.DRAFT)
                .build();
        when(loanService.createLoan(any(), eq("user1"))).thenReturn(dto);

        CustomUserDetails principal = samplePrincipal();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        mockMvc.perform(post("/api/loans")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(loanService).createLoan(any(), eq("user1"));
    }

    @Test
    void getLoan_returnsDto() throws Exception {
        LoanResponseDto dto = LoanResponseDto.builder()
                .id("1")
                .clientName("Client")
                .status(LoanStatus.DRAFT)
                .build();
        when(loanService.getLoan("1")).thenReturn(dto);

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Client"));
    }

    @Test
    void updateLoan_callsServiceWithRole() throws Exception {
        LoanUpdateRequest req = new LoanUpdateRequest();
        req.setClientName("Client");
        req.setLoanType("TermLoan");
        req.setRequestedAmount(10_000L);
        req.setProposedInterestRate(11.0);
        req.setTenureMonths(12);
        FinancialsDto fin = new FinancialsDto();
        fin.setRevenue(1L);
        fin.setEbitda(1L);
        fin.setRating("A");
        req.setFinancials(fin);

        LoanResponseDto dto = LoanResponseDto.builder()
                .id("1")
                .clientName("Client")
                .status(LoanStatus.DRAFT)
                .build();
        when(loanService.updateLoan(eq("1"), any(), eq("user1"), eq(Role.USER)))
                .thenReturn(dto);

        CustomUserDetails principal = samplePrincipal();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        mockMvc.perform(put("/api/loans/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(loanService).updateLoan(eq("1"), any(), eq("user1"), eq(Role.USER));
    }

    @Test
    void updateStatus_callsServiceWithRole() throws Exception {
        LoanStatusUpdateRequest req = new LoanStatusUpdateRequest();
        req.setStatus(LoanStatus.SUBMITTED);
        req.setComments("submit");

        LoanResponseDto dto = LoanResponseDto.builder()
                .id("1")
                .status(LoanStatus.SUBMITTED)
                .build();
        when(loanService.changeStatus(eq("1"), any(), eq("user1"), eq(Role.USER)))
                .thenReturn(dto);

        CustomUserDetails principal = samplePrincipal();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        mockMvc.perform(patch("/api/loans/1/status")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        verify(loanService).changeStatus(eq("1"), any(), eq("user1"), eq(Role.USER));
    }

    @Test
    void listLoans_returnsPaged() throws Exception {
        PagedResponse<LoanResponseDto> res = PagedResponse.<LoanResponseDto>builder()
                .content(List.of(LoanResponseDto.builder().id("1").build()))
                .page(0).size(10).totalElements(1).totalPages(1)
                .build();
        when(loanService.listLoans(0, 10, null, null)).thenReturn(res);

        mockMvc.perform(get("/api/loans")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
