package org.brusnitsyn.financetracker.model.dto;

import lombok.Builder;
import lombok.Data;
import org.brusnitsyn.financetracker.model.enums.TransactionType;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private TransactionType type;
}
