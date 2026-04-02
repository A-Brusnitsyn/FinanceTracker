package org.brusnitsyn.financetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.brusnitsyn.financetracker.model.enums.TransactionType;

@Data
@Builder
public class CategoryCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    @Schema(description = "Transaction type (INCOME or EXPENSE)", example = "INCOME", required = true)
    private TransactionType type;
}
