package org.zetaproject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.zetaproject.services.JwtService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String secretKey = "mysecretkey123456789012345678901234567890"; // match your service

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService.setSecretKey(secretKey); // if your service has setter or config injection
    }

    private String createTestToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hr
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    @Test
    void testGenerateToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void testExtractUsername() {
        String token = createTestToken("john_doe");
        String username = jwtService.extractUsername(token);

        assertEquals("john_doe", username);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("validuser")
                .password("pass")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("validuser")
                .password("pass")
                .roles("USER")
                .build();

        // Token for another user
        String token = createTestToken("someone_else");

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("expireduser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // issued 2 hrs ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // expired 1 hr ago
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("expireduser")
                .password("pass")
                .roles("USER")
                .build();

        assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
    }
}
