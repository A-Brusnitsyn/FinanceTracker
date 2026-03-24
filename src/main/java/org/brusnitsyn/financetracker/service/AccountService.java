package org.brusnitsyn.financetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.mappers.AccountMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public List<AccountResponse> getUserAccounts(Long userId){
        log.info("Fetching accounts for userId={}", userId);

        List<AccountResponse> accounts = accountRepository.findByUserId(userId)
                .stream()
                .map(accountMapper::AccountToResponse)
                .toList();
        log.info("Found {} accounts for userId={}", accounts.size(),userId);

        return accounts;
    }
}
