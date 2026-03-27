package org.brusnitsyn.financetracker.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountRequest {

    @NotBlank
    @Size(max=15)
    private String name;
}
