package edu.eci.cvds.ecireserves.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuarios", description = "Operaciones sobre los usuarios del sistema")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios del sistema")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuarios obtenidos exitosamente", userService.getAllUsers()));
    }


    @GetMapping("/user/users/{id}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener usuario por ID", description = "Un usuario solo puede consultar su propio perfil.")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @Parameter(description = "ID del usuario a buscar", required = true) @PathVariable("id") String id,
            Authentication authentication)   // ← parámetro nuevo
            throws EciReservesException {

        User currentUser = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new EciReservesException(EciReservesException.USER_NOT_FOUND));

        if (!currentUser.getId().equals(id) && !isAdmin(authentication)) {
            throw new AccessDeniedException("No tienes permiso para consultar el perfil de otro usuario.");
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario con id: " + id, userService.getUserById(id)));
    }

    @GetMapping("/user/users/search")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar usuarios por nombre", description = "Busca usuarios en el sistema según su nombre.")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByName(
            @Parameter(description = "Nombre del usuario a buscar", required = true) @RequestParam String name) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuarios con nombre: " + name , userService.getUsersByName(name)));
    }

    @GetMapping("/user/users/email")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar usuario por correo electrónico", description = "Busca un usuario en el sistema según su correo electrónico.")
    public ResponseEntity<ApiResponse<Optional<User>>> getUserByEmail(
        @Parameter(description = "Correo electrónico del usuario a buscar", required = true) @RequestParam String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Usuario no encontrado", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario encontrado", user));
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema.")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Parameter(description = "Datos del usuario a crear", required = true) @RequestBody UserDTO userDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario creado", userService.createUser(userDTO)));
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente en el sistema.")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true) @PathVariable("id") String id,
            @Parameter(description = "Datos actualizados del usuario", required = true) @RequestBody UserDTO userDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario actualizado", userService.updateUser(id, userDTO)));
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true) @PathVariable("id") String id)
            throws EciReservesException {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario eliminado", null));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
    
}