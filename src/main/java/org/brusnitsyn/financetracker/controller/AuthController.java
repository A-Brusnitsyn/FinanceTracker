package org.brusnitsyn.financetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.LoginRequest;
import org.brusnitsyn.financetracker.model.dto.RegistrationRequest;
import org.brusnitsyn.financetracker.model.dto.TokenResponse;
import org.brusnitsyn.financetracker.service.AuthService;
import org.brusnitsyn.financetracker.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with email, password and name")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "User successfully registered"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(
                        responseCode = "409",
                        description = "User with this email already exists")
            })
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegistrationRequest request) {
        log.info("Registering user request");
        return authService.registerUser(request);
    }

    @Operation(summary = "Login", description = "Authenticate in system")
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        log.info("Login user={}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtService.generateToken(request.getEmail());
        return new TokenResponse(token);
    }
}
