package edu.eci.cvds.ecireserves.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.eci.cvds.ecireserves.dto.UserDTO;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Ables to login a user
     * @param email
     * @param password
     * @return
     * @throws EciReservesException
     */
    public User login(String email, String password) throws EciReservesException{
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())){
            throw new EciReservesException(EciReservesException.INVALID_CREDENTIALS);
        }
        return userOpt.get();
    }

    /**
     * Ables to register a user
     * @param userDTO
     * @return
     * @throws EciReservesException
     */
    public User register(UserDTO userDTO) throws EciReservesException{
        if(userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new EciReservesException(EciReservesException.USER_EMAIL_ALREADY_EXISTS);
        }
        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getRol());
        return userRepository.save(user);
    }
}
