package org.brusnitsyn.financetracker.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String currency;
}
