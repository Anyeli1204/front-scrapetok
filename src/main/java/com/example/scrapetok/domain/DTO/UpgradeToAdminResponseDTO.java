package com.example.scrapetok.domain.DTO;

import com.example.scrapetok.domain.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpgradeToAdminResponseDTO {
    private Long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate creationDate;
    private LocalDate admisionToAdminDate;
    private LocalTime admisionToAdminTime;
    private Integer totalQuestionsAnswered;
    private Boolean isActive = true;
}
