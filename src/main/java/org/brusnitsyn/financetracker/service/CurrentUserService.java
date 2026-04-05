package org.brusnitsyn.financetracker.service;

import org.brusnitsyn.financetracker.exception.UserNotFoundException;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
