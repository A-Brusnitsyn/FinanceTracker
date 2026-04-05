package org.brusnitsyn.financetracker.model.dto;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreateRequest {

    @NotNull
    private Long accountId;

    @NotNull
    private Long categoryId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;
}
