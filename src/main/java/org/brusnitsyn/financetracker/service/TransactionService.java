package org.brusnitsyn.financetracker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.TransactionCreateRequest;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.Transaction;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.brusnitsyn.financetracker.repository.TransactionRepository;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTransaction(TransactionCreateRequest request) {
        log.info("Creating transaction userId={}, accountId={}, categoryId={}, amount={}",
                request.getUserId(), request.getAccountId(), request.getCategoryId(), request.getAmount());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByIdAndUserId(request.getAccountId(), request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        BigDecimal newBalance;

        if (category.getType() == TransactionType.INCOME) {
            newBalance = account.getBalance().add(request.getAmount());
        } else {
            newBalance = account.getBalance().subtract(request.getAmount());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }
        }

        account.setBalance(newBalance);

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .category(category)
                .amount(request.getAmount())
                .type(category.getType())
                .description(request.getDescription())
                .date(LocalDate.now())
                .build();
        transactionRepository.save(transaction);
    }
}
