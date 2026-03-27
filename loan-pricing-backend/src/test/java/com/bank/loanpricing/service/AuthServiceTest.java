package com.bank.loanpricing.service;

import com.bank.loanpricing.model.User;
import com.bank.loanpricing.repository.UserRepository;
import com.bank.loanpricing.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setEmail("admin@bank.com");
        user.setPassword("encoded");
        user.setActive(true);

        when(userRepository.findByEmail("admin@bank.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded"))
                .thenReturn(true);
        when(jwtUtil.generateToken(user))
                .thenReturn("jwt-token");

        String token = authService.login("admin@bank.com", "password");

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(userRepository.findByEmail("x@bank.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login("x@bank.com", "pwd"));
    }

    @Test
    void shouldFailWhenUserInactive() {
        User user = new User();
        user.setActive(false);

        when(userRepository.findByEmail("a@bank.com"))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.login("a@bank.com", "pwd"));
    }

    @Test
    void shouldFailWhenPasswordInvalid() {
        User user = new User();
        user.setPassword("encoded");
        user.setActive(true);

        when(userRepository.findByEmail("a@bank.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login("a@bank.com", "wrong"));
    }
}
