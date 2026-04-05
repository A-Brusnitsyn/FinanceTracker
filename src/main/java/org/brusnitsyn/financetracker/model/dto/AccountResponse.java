package org.brusnitsyn.financetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Account response DTO")
public class AccountResponse {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String currency;
}
