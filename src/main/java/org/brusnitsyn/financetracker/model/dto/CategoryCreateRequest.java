package org.brusnitsyn.financetracker.model.dto;

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
    private TransactionType type;

    @NotNull
    private Long userId;
}
