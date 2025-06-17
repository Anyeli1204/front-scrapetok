package com.example.scrapetok.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String password;
    private String username;
    private String role;
    private String token;
}