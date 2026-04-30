package edu.eci.cvds.ecireserves.service;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("1", "John Doe", "john.doe@example.com", "password123", UserRole.ADMINISTRADOR);
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john.doe@example.com");

        assertNotNull(userDetails);
        assertEquals("john.doe@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals(Collections.singletonList("ROLE_ADMINISTRADOR"), userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));
        assertNotNull(exception.getMessage());
    }
}
