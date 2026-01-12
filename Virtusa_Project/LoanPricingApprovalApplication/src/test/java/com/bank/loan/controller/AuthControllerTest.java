package com.bank.loan.controller;

import com.bank.loan.dto.AuthRequest;
import com.bank.loan.model.Role;
import com.bank.loan.model.User;
import com.bank.loan.security.JwtTokenProvider;
import com.bank.loan.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_returnsToken() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setEmail("rm@bank.com");
        req.setPassword("pass");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "rm@bank.com", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generateToken(eq("rm@bank.com"), eq("USER"))).thenReturn("TOKEN");

        String json = """
                {"email":"rm@bank.com","password":"pass"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("TOKEN"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void me_returnsCurrentUser() throws Exception {
        var context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "rm@bank.com", null, List.of());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(userService.getCurrentUser("rm@bank.com"))
                .thenReturn(com.bank.loan.dto.UserResponseDto.builder()
                        .id("1")
                        .email("rm@bank.com")
                        .role(Role.USER)
                        .active(true)
                        .build());

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rm@bank.com"));
    }
}
