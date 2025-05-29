package com.example.scrapetok.domain.DTO;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AdminSystemResponseDTO {
    private Long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String username;
    private LocalDate creationDate;
    private LocalDate admisionToAdminDate;
    private LocalTime admisionToAdminTime;
    private Integer totalQuestionsAnswered;
    private Boolean isActive;
}
