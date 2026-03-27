package com.bank.loanpricing.security;

import com.bank.loanpricing.model.User;
import com.bank.loanpricing.model.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Inject values normally read from application-docker.yml
        ReflectionTestUtils.setField(
                jwtUtil,
                "secret",
                "mytestsecretkeymytestsecretkey12345"
        );
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1000 * 60 * 60L);
    }

    @Test
    void shouldGenerateAndParseToken() {
        User user = new User();
        user.setId("user123");
        user.setRole(Role.ADMIN);

        String token = jwtUtil.generateToken(user);
        assertNotNull(token);

        Claims claims = jwtUtil.extractClaims(token);
        assertEquals("user123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
    }
}
