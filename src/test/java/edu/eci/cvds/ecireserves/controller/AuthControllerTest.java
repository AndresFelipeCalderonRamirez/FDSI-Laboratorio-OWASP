package edu.eci.cvds.ecireserves.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.eci.cvds.ecireserves.dto.AuthRequest;
import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.repository.UserRepository;
import edu.eci.cvds.ecireserves.service.CustomUserDetailsService;
import edu.eci.cvds.ecireserves.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("1", "John Doe", "johndoe@example.com", "password", UserRole.ESTUDIANTE);
        user = new User("1", "John Doe", "johndoe@example.com", "encodedPassword", UserRole.ESTUDIANTE);
    }

    @SuppressWarnings("null")
    @Test
    void registerUser_ShouldReturnSuccess_WhenUserNotExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<ApiResponse<String>> response = authController.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Usuario registrado exitosamente", response.getBody().getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @SuppressWarnings("null")
    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailAlreadyRegistered() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<String>> response = authController.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("El email ya está registrado", response.getBody().getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @SuppressWarnings("null")
    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() {
        AuthRequest authRequest = new AuthRequest("johndoe@example.com", "password");
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(authRequest.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        ResponseEntity<ApiResponse<String>> response = authController.loginUser(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("mocked-jwt-token", response.getBody().getMessage());
    }

    @Test
    void loginUser_ShouldThrowException_WhenCredentialsAreInvalid() {
        AuthRequest authRequest = new AuthRequest("johndoe@example.com", "wrong-password");

        doThrow(new BadCredentialsException("Invalid credentials"))
            .when(authenticationManager)
            .authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authController.loginUser(authRequest);
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
