package org.brusnitsyn.financetracker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.exception.AccountNotFoundException;
import org.brusnitsyn.financetracker.exception.CategoryNotFoundException;
import org.brusnitsyn.financetracker.exception.InsufficientFundsException;
import org.brusnitsyn.financetracker.model.dto.TransactionCreateRequest;
import org.brusnitsyn.financetracker.model.dto.TransactionResponse;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.Transaction;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.model.mappers.TransactionMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.brusnitsyn.financetracker.repository.TransactionRepository;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public void createTransaction(TransactionCreateRequest request) {
        User user =currentUserService.getCurrentUser();
        log.info("Creating transaction user={}, accountId={}, categoryId={}, amount={}",
                user.getEmail(), request.getAccountId(), request.getCategoryId(), request.getAmount());

        Account account = accountRepository.findByIdAndUser(request.getAccountId(), user)
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

        Category category = categoryRepository.findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        BigDecimal newBalance;

        if (category.getType() == TransactionType.INCOME) {
            newBalance = account.getBalance().add(request.getAmount());
        } else {
            newBalance = account.getBalance().subtract(request.getAmount());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException();
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

    public List<TransactionResponse> getTransactions(Long accountId, Long categoryId, TransactionType type, LocalDate fromDate, LocalDate toDate) {
        User user =currentUserService.getCurrentUser();
        log.info("Fetching transactions for user={}, accountId={}, categoryId={} type={}, from={}, to={}", user.getEmail(), accountId, categoryId, type, fromDate, toDate);

        List<Transaction> transactions = transactionRepository.findByUser(user);

        return transactions.stream()
                .filter(transaction -> accountId==null || transaction.getAccount().getId().equals(accountId))
                .filter(transaction -> categoryId==null || transaction.getCategory().getId().equals(categoryId))
                .filter(transaction -> type == null || transaction.getType()==type)
                .filter(transaction -> fromDate==null || !transaction.getDate().isBefore(fromDate))
                .filter(transaction -> toDate==null || !transaction.getDate().isAfter(toDate))
                .map(transactionMapper::transactionToResponse)
                .toList();
    }
}
