package org.brusnitsyn.financetracker.model.mappers;

import org.brusnitsyn.financetracker.model.dto.AccountResponse;
import org.brusnitsyn.financetracker.model.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountResponse accountToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
