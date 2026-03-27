package org.brusnitsyn.financetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.dto.CreateAccountRequest;
import org.brusnitsyn.financetracker.model.dto.UpdateAccountRequest;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.mappers.AccountMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<AccountResponse> getUserAccounts(Long userId) {
        log.info("Fetching accounts for userId={}", userId);

        List<AccountResponse> accounts = accountRepository.findByUserId(userId)
                .stream()
                .map(accountMapper::accountToResponse)
                .toList();
        log.info("Found {} accounts for userId={}", accounts.size(), userId);

        return accounts;
    }

    public AccountResponse createAccount(Long userId, CreateAccountRequest request) {
        log.info("Creating account for userId={}, name={}", userId, request.getName());

        Account account = Account.builder()
                .user(User.builder().id(userId).build())
                .name(request.getName())
                .currency(request.getCurrency().toUpperCase())
                .balance(BigDecimal.ZERO)
                .build();

        Account savedAccount = accountRepository.save(account);

        log.info("Account created: id={}, userId={}", savedAccount.getId(), userId);

        return accountMapper.accountToResponse(savedAccount);
    }

    public AccountResponse updateAccount(Long userId, Long accountId, UpdateAccountRequest request) {
        log.info("Updating account name for id={}, userId={}", accountId, userId);

        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not find or access denied"));

        account.setName(request.getName());

        Account updatedAccount = accountRepository.save(account);

        log.info("Account updated id={}", updatedAccount.getId());

        return accountMapper.accountToResponse(updatedAccount);
    }

    public void deleteAccount(Long userId, Long accountId){
        log.info("Deleting account id={}, for userId={}", accountId, userId);

        Account account=accountRepository.findByIdAndUserId(accountId,userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not find or access denied"));

        if (account.getBalance().compareTo(BigDecimal.ZERO)!=0){
            throw new IllegalStateException("Account balance must be zero to delete");
        }

        accountRepository.delete(account);

        log.info("Account deleted id={}", accountId);
    }
}
