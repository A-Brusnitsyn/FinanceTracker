package org.brusnitsyn.financetracker.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import org.brusnitsyn.financetracker.model.enums.TransactionType;

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
