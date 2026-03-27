package org.brusnitsyn.financetracker.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAccounts(@RequestParam Long userId) {
        return accountService.getUserAccounts(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestParam Long userId, @Valid @RequestBody CreateAccountRequest request){
        return accountService.createAccount(userId,request);
    }

    @PutMapping("/{id}")
    public AccountResponse updateAccount(@PathVariable Long accountId, @RequestParam Long userId, @Valid @RequestBody UpdateAccountRequest request){
        return accountService.updateAccount(userId, accountId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id, @RequestParam Long userId){
        accountService.deleteAccount(userId, id);
    }
}
