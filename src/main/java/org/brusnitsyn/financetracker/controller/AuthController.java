package org.brusnitsyn.financetracker.controller;

import jakarta.validation.Valid;
import org.brusnitsyn.financetracker.model.dto.RegistrationRequest;
import org.brusnitsyn.financetracker.model.dto.UserResponse;
import org.brusnitsyn.financetracker.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegistrationRequest request){
        return authService.registerUser(request);
    }
}
