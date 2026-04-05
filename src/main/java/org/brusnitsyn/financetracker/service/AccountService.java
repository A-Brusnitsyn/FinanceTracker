package org.brusnitsyn.financetracker.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.exception.AccountHasBalanceException;
import org.brusnitsyn.financetracker.exception.AccountNotFoundException;
import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.dto.CreateAccountRequest;
import org.brusnitsyn.financetracker.model.dto.UpdateAccountRequest;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.mappers.AccountMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrentUserService currentUserService;

    public AccountService(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            CurrentUserService currentUserService) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.currentUserService = currentUserService;
    }

    public List<AccountResponse> getUserAccounts() {
        User user = currentUserService.getCurrentUser();
        log.info("Fetching accounts for user={}", user.getEmail());
        List<AccountResponse> accounts =
                accountRepository.findByUser(user).stream()
                        .map(accountMapper::accountToResponse)
                        .toList();
        log.info("Found {} accounts for user={}", accounts.size(), user.getEmail());

        return accounts;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = currentUserService.getCurrentUser();
        log.info("Creating account for user={}, name={}", user.getEmail(), request.getName());

        Account account =
                Account.builder()
                        .user(user)
                        .name(request.getName())
                        .currency(request.getCurrency().toUpperCase())
                        .balance(BigDecimal.ZERO)
                        .build();

        Account savedAccount = accountRepository.save(account);

        log.info("Account created: id={}, user={}", savedAccount.getId(), user.getEmail());

        return accountMapper.accountToResponse(savedAccount);
    }

    @Transactional
    public AccountResponse updateAccount(Long accountId, UpdateAccountRequest request) {
        User user = currentUserService.getCurrentUser();
        log.info("Updating account name for id={}, user={}", accountId, user.getEmail());

        Account account =
                accountRepository
                        .findByIdAndUser(accountId, user)
                        .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setName(request.getName());

        Account updatedAccount = accountRepository.save(account);

        log.info("Account updated id={}", updatedAccount.getId());

        return accountMapper.accountToResponse(updatedAccount);
    }

    public void deleteAccount(Long accountId) {
        User user = currentUserService.getCurrentUser();
        log.info("Deleting account id={}, for user={}", accountId, user);

        Account account =
                accountRepository
                        .findByIdAndUser(accountId, user)
                        .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountHasBalanceException(accountId);
        }

        accountRepository.delete(account);

        log.info("Account deleted id={}", accountId);
    }
}
