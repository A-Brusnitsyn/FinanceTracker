package org.brusnitsyn.financetracker.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreateRequest {

    @NotNull private Long accountId;

    @NotNull private Long categoryId;

    @NotNull @Positive private BigDecimal amount;

    private String description;
}
