package edu.eci.cvds.ecireserves.util;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenExpiration() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.extractExpiration(token).before(new Date()));
    }

    @Test
    void testValidateToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testInvalidToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = new User("otheruser", "password", userDetails.getAuthorities());
        assertFalse(jwtUtil.validateToken(token, differentUser));
    }
}

