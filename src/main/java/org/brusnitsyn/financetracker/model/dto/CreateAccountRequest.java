package org.brusnitsyn.financetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Account create")
public class CreateAccountRequest {

    @NotBlank
    @Size(max=15)
    private String name;

    @NotBlank
    @Size(min=3,max=3)
    private String currency;
}
