package com.bank.loan.controller;

import com.bank.loan.exception.GlobalExceptionHandler;
import com.bank.loan.model.Role;
import com.bank.loan.model.User;
import com.bank.loan.security.CustomUserDetails;
import com.bank.loan.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.*;

import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminLoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private AdminLoanController adminLoanController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminLoanController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

    }

    @Test
    void deleteLoan_callsService() throws Exception {
        User user = User.builder()
                .id("admin1")
                .email("admin@bank.com")
                .password("ENC")
                .role(Role.ADMIN)
                .active(true)
                .build();
        CustomUserDetails principal = new CustomUserDetails(user);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        mockMvc.perform(delete("/api/admin/loans/1").principal(auth))
                .andExpect(status().isNoContent());

        verify(loanService).softDeleteLoan("1", "admin1");
    }
}

