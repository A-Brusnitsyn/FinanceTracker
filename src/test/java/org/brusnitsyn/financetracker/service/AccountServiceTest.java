package org.brusnitsyn.financetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.brusnitsyn.financetracker.PostgresTestContainer;
import org.brusnitsyn.financetracker.exception.AccountHasBalanceException;
import org.brusnitsyn.financetracker.exception.AccountNotFoundException;
import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.dto.CreateAccountRequest;
import org.brusnitsyn.financetracker.model.dto.UpdateAccountRequest;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.Role;
import org.brusnitsyn.financetracker.model.mappers.AccountMapper;
import org.brusnitsyn.financetracker.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest implements PostgresTestContainer {

    @Mock private AccountRepository accountRepository;

    @Mock private AccountMapper accountMapper;

    @Mock private CurrentUserService currentUserService;

    @InjectMocks private AccountService accountService;

    private User testUser;
    private Account testAccount;
    private AccountResponse testAccountResponse;
    private CreateAccountRequest createRequest;
    private UpdateAccountRequest updateRequest;

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
                        .balance(BigDecimal.ZERO)
                        .currency("USD")
                        .build();

        testAccountResponse =
                AccountResponse.builder()
                        .id(1L)
                        .name("Main Account")
                        .balance(BigDecimal.ZERO)
                        .currency("USD")
                        .build();

        createRequest = new CreateAccountRequest();
        createRequest.setName("New Account");
        createRequest.setCurrency("EUR");

        updateRequest = new UpdateAccountRequest();
        updateRequest.setName("Updated Account");
    }

    @Test
    void getUserAccounts_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUser(testUser)).thenReturn(List.of(testAccount));
        when(accountMapper.accountToResponse(testAccount)).thenReturn(testAccountResponse);

        // When
        List<AccountResponse> result = accountService.getUserAccounts();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Main Account");

        verify(accountRepository).findByUser(testUser);
        verify(accountMapper).accountToResponse(testAccount);
    }

    @Test
    void getUserAccounts_EmptyList_WhenNoAccounts() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByUser(testUser)).thenReturn(List.of());

        // When
        List<AccountResponse> result = accountService.getUserAccounts();

        // Then
        assertThat(result).isEmpty();
        verify(accountRepository).findByUser(testUser);
        verify(accountMapper, never()).accountToResponse(any());
    }

    @Test
    void createAccount_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.accountToResponse(testAccount)).thenReturn(testAccountResponse);

        // When
        AccountResponse result = accountService.createAccount(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Main Account");

        verify(accountRepository).save(any(Account.class));
        verify(accountMapper).accountToResponse(testAccount);
    }

    @Test
    void createAccount_ConvertsCurrencyToUpperCase() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        request.setName("Test Account");
        request.setCurrency("usd"); // lowercase

        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(
                        invocation -> {
                            Account saved = invocation.getArgument(0);
                            saved.setId(2L);
                            return saved;
                        });
        when(accountMapper.accountToResponse(any(Account.class)))
                .thenReturn(
                        AccountResponse.builder()
                                .id(2L)
                                .name("Test Account")
                                .currency("USD")
                                .build());

        // When
        AccountResponse result = accountService.createAccount(request);

        // Then
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void updateAccount_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.accountToResponse(testAccount)).thenReturn(testAccountResponse);

        // When
        AccountResponse result = accountService.updateAccount(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(testAccount.getName()).isEqualTo("Updated Account");

        verify(accountRepository).save(testAccount);
    }

    @Test
    void updateAccount_ThrowsException_WhenAccountNotFound() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.updateAccount(999L, updateRequest))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found: id=999");

        verify(accountRepository, never()).save(any());
    }

    @Test
    void deleteAccount_Success() {
        // Given
        testAccount.setBalance(BigDecimal.ZERO);
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));

        // When
        accountService.deleteAccount(1L);

        // Then
        verify(accountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_ThrowsException_WhenAccountHasBalance() {
        // Given
        testAccount.setBalance(BigDecimal.valueOf(100.50));
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testAccount));

        // When & Then
        assertThatThrownBy(() -> accountService.deleteAccount(1L))
                .isInstanceOf(AccountHasBalanceException.class)
                .hasMessageContaining("Account has non-zero balance: id=1");

        verify(accountRepository, never()).delete(any());
    }

    @Test
    void deleteAccount_ThrowsException_WhenAccountNotFound() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(accountRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.deleteAccount(999L))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository, never()).delete(any());
    }
}
