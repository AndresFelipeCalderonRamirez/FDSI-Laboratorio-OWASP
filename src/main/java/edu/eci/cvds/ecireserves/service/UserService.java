package edu.eci.cvds.ecireserves.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.enums.UserRole;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Encode password using BCrypt. Centralizes all password hashing.
     * Never call setPassword() directly without this method.
     * @param rawPassword Raw password
     * @return Encoded password
     */
    private String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Get all users
     * @return List of users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by id
     * @param id User id
     * @return User
     * @throws EciReservesException
     */
    public User getUserById(String id) throws EciReservesException {
        return userRepository.findById(id).orElseThrow(() -> new EciReservesException(EciReservesException.USER_NOT_FOUND));
    }

    /**
     * Get users by name
     * @param name User name
     * @return List of users
     */
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }

    /**
     * Get users by role
     * @param rol User role
     * @return List of users
     */
    public List<User> getUsersByRol(UserRole rol) {
        return userRepository.findByRol(rol);
    }

    /**
     * Create a new user
     * @param userDTO User data
     * @return User
     * @throws EciReservesException
     */
    public User createUser(UserDTO userDTO) throws EciReservesException {
        if (userDTO.getName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getRol() == null) {
            throw new EciReservesException(EciReservesException.USER_DATA_NOT_COMPLETE);
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EciReservesException(EciReservesException.USER_EMAIL_ALREADY_EXISTS);
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodePassword(userDTO.getPassword()));
        user.setRol(userDTO.getRol());

        return userRepository.save(user);
    }

    /**
     * Update user
     * @param id User id
     * @param userDTO User data
     * @return User
     * @throws EciReservesException
     */
    public User updateUser(String id, UserDTO userDTO) throws EciReservesException {
        User user = userRepository.findById(id).orElseThrow(() -> new EciReservesException(EciReservesException.USER_NOT_FOUND));
        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getEmail() != null && !user.getEmail().equals(userDTO.getEmail())) {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new EciReservesException(EciReservesException.USER_EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) user.setPassword(encodePassword(userDTO.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Delete user
     * @param id User id
     * @throws EciReservesException
     */
    public void deleteUser(String id) throws EciReservesException {
        if (!userRepository.existsById(id)) {
            throw new EciReservesException(EciReservesException.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}