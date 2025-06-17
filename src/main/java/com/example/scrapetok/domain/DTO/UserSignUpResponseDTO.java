package com.example.scrapetok.domain.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignUpResponseDTO {
    private Long id;
    private String email;
    private String password;
    private String username;
    private String role;
    private String token;
}

