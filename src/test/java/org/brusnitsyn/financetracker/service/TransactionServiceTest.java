package org.brusnitsyn.financetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.brusnitsyn.financetracker.exception.AccountNotFoundException;
import org.brusnitsyn.financetracker.exception.CategoryNotFoundException;
import org.brusnitsyn.financetracker.exception.InsufficientFundsException;
import org.brusnitsyn.financetracker.model.dto.TransactionCreateRequest;
import org.brusnitsyn.financetracker.model.dto.TransactionResponse;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.Transaction;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.Role;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.model.mappers.TransactionMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.brusnitsyn.financetracker.repository.TransactionRepository;
import org.brusnitsyn.financetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private AccountRepository accountRepository;

    @Mock private CategoryRepository categoryRepository;

    @Mock private TransactionRepository transactionRepository;

    @Mock private UserRepository userRepository;

    @Mock private TransactionMapper transactionMapper;

    @Mock private CurrentUserService currentUserService;

    @InjectMocks private TransactionService transactionService;

    private User testUser;
    private Account testAccount;
    private Category incomeCategory;
    private Category expenseCategory;
    private Transaction testTransaction;
    private TransactionResponse testTransactionResponse;
    private TransactionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser =
                User.builder()
                        .id(1L)
                        .email("test@example.com")
                        .name("Test User")
                        .role(Role.USER)
                        .build();

        testAccount =
                Account.builder()
                        .id(1L)
                        .user(testUser)
                        .name("Main Account")
                        .balance(BigDecimal.valueOf(1000))
                        .currency("USD")
                        .build();

        incomeCategory =
                Category.builder()
                        .id(1L)
                        .user(testUser)
                        .name("Salary")
                        .type(TransactionType.INCOME)
                        .build();

        expenseCategory =
                Category.builder()
                        .id(2L)
                        .user(testUser)
                        .name("Groceries")
                        .type(TransactionType.EXPENSE)
                        .build();

        testTransaction =
                Transaction.builder()
                        .id(1L)
                        .user(testUser)
                        .account(testAccount)
                        .category(expenseCategory)
                        .amount(BigDecimal.valueOf(50))
                        .type(TransactionType.EXPENSE)
                        .description("Test transaction")
                        .date(LocalDate.now())
                        .build();

        testTransactionResponse =
                TransactionResponse.builder()
                        .id(1L)
                        .accountId(1L)
                        .categoryId(2L)
                        .type(TransactionType.EXPENSE)
                        .amount(BigDecimal.valueOf(50))
                        .description("Test transaction")
                        .date(LocalDate.now())
                        .build();

        createRequest = new TransactionCreateRequest();
        createRequest.setAccountId(1L);
        createRequest.setCategoryId(2L);
        createRequest.setAmount(BigDecimal.valueOf(50));
        createRequest.setDescription("Test transaction");
    }

    @Test
    void createTransaction_Expense_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findByIdAndUser(2L, testUser))
                .thenReturn(Optional.of(expenseCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        transactionService.createTransaction(createRequest);

        // Then
        assertThat(testAccount.getBalance()).isEqualTo(BigDecimal.valueOf(950)); // 1000 - 50
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Income_Success() {
        // Given
        createRequest.setCategoryId(1L);
        createRequest.setAmount(BigDecimal.valueOf(200));

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.of(incomeCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        transactionService.createTransaction(createRequest);

        // Then
        assertThat(testAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1200)); // 1000 + 200
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ThrowsException_WhenInsufficientFunds() {
        // Given
        createRequest.setAmount(BigDecimal.valueOf(2000)); // More than balance

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findByIdAndUser(2L, testUser))
                .thenReturn(Optional.of(expenseCategory));

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(createRequest))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Insufficient funds");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_ThrowsException_WhenAccountNotFound() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        createRequest.setAccountId(999L);

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(createRequest))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found: id=999");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_ThrowsException_WhenCategoryNotFound() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        createRequest.setCategoryId(999L);

        // When & Then
        assertThatThrownBy(() -> transactionService.createTransaction(createRequest))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found: id=999");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransactions_Success_WithoutFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 20, Sort.by("date").descending());
        Page<Transaction> transactionPage = new PageImpl<>(List.of(testTransaction), pageable, 1);

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(transactionRepository.findUserTransactions(
                        eq(testUser),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)))
                .thenReturn(transactionPage);
        when(transactionMapper.transactionToResponse(testTransaction))
                .thenReturn(testTransactionResponse);

        // When
        Page<TransactionResponse> result =
                transactionService.getTransactions(0, 20, null, null, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);

        verify(transactionRepository)
                .findUserTransactions(
                        any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getTransactions_WithDateFilter() {
        // Given
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        Page<Transaction> transactionPage = new PageImpl<>(List.of(testTransaction), pageable, 1);

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(transactionRepository.findUserTransactions(
                        eq(testUser),
                        eq(from),
                        eq(to),
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)))
                .thenReturn(transactionPage);
        when(transactionMapper.transactionToResponse(testTransaction))
                .thenReturn(testTransactionResponse);

        // When
        Page<TransactionResponse> result =
                transactionService.getTransactions(0, 10, from, to, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTransactions_WithAccountFilter_ThrowsException_WhenAccountBelongsToOtherUser() {
        // Given
        User otherUser = User.builder().id(999L).build();
        Account otherAccount = Account.builder().id(1L).user(otherUser).build();

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(otherAccount));

        // When & Then
        assertThatThrownBy(
                        () -> transactionService.getTransactions(0, 20, null, null, null, 1L, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Account does not belong to user");
    }

    @Test
    void getTransactions_WithTypeFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 20, Sort.by("date").descending());
        Page<Transaction> transactionPage = new PageImpl<>(List.of(testTransaction), pageable, 1);

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(transactionRepository.findUserTransactions(
                        eq(testUser),
                        isNull(),
                        isNull(),
                        eq(TransactionType.EXPENSE),
                        isNull(),
                        isNull(),
                        any(Pageable.class)))
                .thenReturn(transactionPage);
        when(transactionMapper.transactionToResponse(testTransaction))
                .thenReturn(testTransactionResponse);

        // When
        Page<TransactionResponse> result =
                transactionService.getTransactions(
                        0, 20, null, null, TransactionType.EXPENSE, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTransactions_CustomPagination() {
        // Given
        Pageable pageable = PageRequest.of(2, 5, Sort.by("date").descending());
        Page<Transaction> transactionPage = new PageImpl<>(List.of(), pageable, 0);

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(transactionRepository.findUserTransactions(
                        eq(testUser),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)))
                .thenReturn(transactionPage);

        // When
        Page<TransactionResponse> result =
                transactionService.getTransactions(2, 5, null, null, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable().getPageNumber()).isEqualTo(2);
        assertThat(result.getPageable().getPageSize()).isEqualTo(5);
    }
}
