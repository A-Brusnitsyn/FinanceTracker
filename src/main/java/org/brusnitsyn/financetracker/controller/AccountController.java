package org.brusnitsyn.financetracker.controller;

import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAccounts(@RequestParam Long userId){
        return accountService.getUserAccounts(userId);
    }
}
