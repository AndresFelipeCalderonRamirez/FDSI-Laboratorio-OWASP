package edu.eci.cvds.ecireserves.controller;

import edu.eci.cvds.ecireserves.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import edu.eci.cvds.ecireserves.dto.AuthRequest;
import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.repository.UserRepository;
import edu.eci.cvds.ecireserves.service.CustomUserDetailsService;
import edu.eci.cvds.ecireserves.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, 
                          CustomUserDetailsService userDetailsService, 
                          UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario en el sistema si el email no está en uso.")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @Parameter(description = "Datos del usuario a registrar", required = true) @RequestBody UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
        if (user != null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "El email ya está registrado", null));
        } else {
            if(userDTO.getName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getRol() == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Los campos no pueden estar vacíos", null));
            }
            user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail(), 
                            passwordEncoder.encode(userDTO.getPassword()), userDTO.getRol());
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse<>(true, "Usuario registrado exitosamente", null));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticación de usuario", description = "Autentica un usuario y devuelve un token JWT si las credenciales son válidas.")
    public ResponseEntity<ApiResponse<String>> loginUser(
            @Parameter(description = "Credenciales de inicio de sesión", required = true) @RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, jwt, null));
    }
}