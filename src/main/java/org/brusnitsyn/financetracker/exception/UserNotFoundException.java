package org.brusnitsyn.financetracker.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found: id=" + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found: email=" + email);
    }
}
