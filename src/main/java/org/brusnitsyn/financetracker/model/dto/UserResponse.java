package org.brusnitsyn.financetracker.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
}
