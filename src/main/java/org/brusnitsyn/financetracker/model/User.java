package org.brusnitsyn.financetracker.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDateTime created;


}
