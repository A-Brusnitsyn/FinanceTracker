package org.brusnitsyn.financetracker.model.dto;

import lombok.Builder;
import lombok.Data;
import org.brusnitsyn.financetracker.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private Long categoryId;
    private Long accountId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

}
