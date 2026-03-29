package org.brusnitsyn.financetracker.exception;

public class AccountHasBalanceException extends RuntimeException {
    public AccountHasBalanceException(Long id) {
        super("Account has non-zero balance: id=" + id);
    }
}
