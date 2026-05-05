package edu.eci.cvds.ecireserves.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(new User("1", "John Doe", "john@example.com", "password", UserRole.ESTUDIANTE));
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<ApiResponse<List<User>>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUsersByName_ShouldReturnMatchingUsers() {
        List<User> users = Arrays.asList(new User("1", "John Doe", "john@example.com", "password", null));
        when(userService.getUsersByName("John")).thenReturn(users);

        ResponseEntity<ApiResponse<List<User>>> response = userController.getUsersByName("John");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(userService, times(1)).getUsersByName("John");
    }

    @SuppressWarnings("null")
    @Test
    void createUser_ShouldReturnCreatedUser() throws EciReservesException {
        UserDTO userDTO = new UserDTO("1","John Doe", "john@example.com", "password", UserRole.ESTUDIANTE);
        User createdUser = new User("1", "John Doe", "john@example.com", "password", UserRole.ESTUDIANTE);
        when(userService.createUser(userDTO)).thenReturn(createdUser);

        ResponseEntity<ApiResponse<User>> response = userController.createUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("1", response.getBody().getData().getId());
        verify(userService, times(1)).createUser(userDTO);
    }

    @SuppressWarnings("null")
    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserExist() throws EciReservesException{
        UserDTO userDTO = new UserDTO("1","John Doe", "john@example", "password", UserRole.ESTUDIANTE);
        User updatedUser = new User("1", "John Doe", "john@example", "password", UserRole.ESTUDIANTE);
        when(userService.updateUser("1", userDTO)).thenReturn(updatedUser);

        ResponseEntity<ApiResponse<User>> response = userController.updateUser("1", userDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("1", response.getBody().getData().getId());
        verify(userService, times(1)).updateUser("1", userDTO);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserDoesNotExist() throws EciReservesException {
        UserDTO userDTO = new UserDTO("1","John Doe", "john@example", "password", UserRole.ESTUDIANTE);

        when(userService.updateUser("2", userDTO)).thenThrow(new EciReservesException("Usuario no encontrado"));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> {
            userController.updateUser("2", userDTO);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userService, times(1)).updateUser("2", userDTO);
    }

    @SuppressWarnings("null")
    @Test
    void deleteUser_ShouldReturnSuccessMessage_WhenUserExists() throws EciReservesException {
        doNothing().when(userService).deleteUser("1");

        ResponseEntity<ApiResponse<Void>> response = userController.deleteUser("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Usuario eliminado", response.getBody().getMessage());
        verify(userService, times(1)).deleteUser("1");
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws EciReservesException {
        doThrow(new EciReservesException("Usuario no encontrado")).when(userService).deleteUser("99");

        EciReservesException exception = assertThrows(EciReservesException.class, () -> {
            userController.deleteUser("99");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userService, times(1)).deleteUser("99");
    }

    @SuppressWarnings("null")
    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        User user = new User("1", "John Doe", "john@example.com", "password", UserRole.ESTUDIANTE);
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<Optional<User>>> response = userController.getUserByEmail("john@example.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Usuario encontrado", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isPresent());
        assertEquals("john@example.com", response.getBody().getData().get().getEmail());

        verify(userService, times(1)).getUserByEmail("john@example.com");
    }

    @SuppressWarnings("null")
    @Test
    void getUserByEmail_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userService.getUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<Optional<User>>> response = userController.getUserByEmail("notfound@example.com");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Usuario no encontrado", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(userService, times(1)).getUserByEmail("notfound@example.com");
    }
}
