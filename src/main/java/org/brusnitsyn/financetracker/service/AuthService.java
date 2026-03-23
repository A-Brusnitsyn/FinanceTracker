package org.brusnitsyn.financetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.exception.UserAlreadyExistsException;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.UserMapper;
import org.brusnitsyn.financetracker.model.dto.RegistrationRequest;
import org.brusnitsyn.financetracker.model.dto.UserResponse;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse registerUser(RegistrationRequest request){
        log.info("Registering new user with email: " + request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Attempt to register existing email: " + request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword()) //TODO: encryption password
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User with email " + savedUser.getEmail() + " successfully registered. ID - " + savedUser.getId());

        return userMapper.userToResponse(savedUser);
    }
}
