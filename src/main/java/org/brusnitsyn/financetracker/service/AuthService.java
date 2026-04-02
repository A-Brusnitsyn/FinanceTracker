package org.brusnitsyn.financetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.exception.UserAlreadyExistsException;
import org.brusnitsyn.financetracker.model.dto.TokenResponse;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.mappers.UserMapper;
import org.brusnitsyn.financetracker.model.dto.RegistrationRequest;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserMapper userMapper, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    public TokenResponse registerUser(RegistrationRequest request) {
        log.info("Registering new user with email: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Attempt to register existing email: " + request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User with email " + savedUser.getEmail() + " successfully registered. ID - " + savedUser.getId());
        String token=jwtService.generateToken(user.getEmail());
        return new TokenResponse(token);
    }
}