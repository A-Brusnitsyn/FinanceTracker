package org.brusnitsyn.financetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.dto.CreateAccountRequest;
import org.brusnitsyn.financetracker.model.dto.UpdateAccountRequest;
import org.brusnitsyn.financetracker.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing user financial accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Get user accounts",
            description = "Fetching all accounts of a user from database")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping
    public List<AccountResponse> getAccounts() {
        log.debug("GET /accounts");
        return accountService.getUserAccounts();
    }

    @Operation(
            summary = "Create account for user",
            description = "Creates a new financial account with zero balance")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Account created"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Create account request");
        return accountService.createAccount(request);
    }

    @Operation(
            summary = "Update account name",
            description = "Updates the name of an existing account")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Account updated"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "Account not found")
            })
    @PutMapping("/{id}")
    public AccountResponse updateAccount(
            @PathVariable Long id, @Valid @RequestBody UpdateAccountRequest request) {
        log.info("Update account request");
        return accountService.updateAccount(id, request);
    }

    @Operation(
            summary = "Delete account",
            description = "Deletes an account only if its balance is zero")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Account not found"),
                @ApiResponse(responseCode = "409", description = "Account has non-zero balance")
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        log.info("Delete account request");
        accountService.deleteAccount(id);
    }
}
