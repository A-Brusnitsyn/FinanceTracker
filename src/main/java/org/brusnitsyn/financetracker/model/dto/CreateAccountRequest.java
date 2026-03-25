package org.brusnitsyn.financetracker.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotBlank
    @Size(max=15)
    private String name;

    @NotBlank
    @Size(min=3,max=3)
    private String currency;
}
