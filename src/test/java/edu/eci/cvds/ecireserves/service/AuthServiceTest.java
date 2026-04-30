package edu.eci.cvds.ecireserves.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User("1", "Juan Pérez", "juan@example.com", "encryptedPassword", UserRole.ESTUDIANTE);
        userDTO = new UserDTO("1", "Juan Pérez", "juan@example.com", "123456", UserRole.ESTUDIANTE);
    }

    @Test
    void shouldRegisterNewUser() throws EciReservesException {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = authService.register(userDTO);

        assertNotNull(registeredUser);
        assertEquals(userDTO.getEmail(), registeredUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotRegisterUser_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> authService.register(userDTO));

        assertEquals(EciReservesException.USER_EMAIL_ALREADY_EXISTS, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUser_WhenCredentialsAreCorrect() throws EciReservesException {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);

        User loggedInUser = authService.login(user.getEmail(), userDTO.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(user.getEmail(), loggedInUser.getEmail());
    }

    @Test
    void shouldNotLoginUser_WhenEmailNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> authService.login(user.getEmail(), userDTO.getPassword()));

        assertEquals(EciReservesException.INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void shouldNotLoginUser_WhenPasswordIsIncorrect() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(false);

        EciReservesException exception = assertThrows(EciReservesException.class, () -> authService.login(user.getEmail(), userDTO.getPassword()));

        assertEquals(EciReservesException.INVALID_CREDENTIALS, exception.getMessage());
    }
}
