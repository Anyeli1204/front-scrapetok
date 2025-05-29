package com.example.scrapetok.domain.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignUpResponseDTO {
    private Long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String username;
    private LocalDate creationDate;

}

