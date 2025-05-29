package com.example.scrapetok.domain.DTO;

import com.example.scrapetok.domain.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
public class AdminProfileResponseDTO {
    // Información personal
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate creationDate;

    // Información de Admin
    private LocalDate admisionToAdminDate;
    private LocalTime admisionToAdminTime;
    private Integer totalQuestionsAnswered;
    private Boolean isActive = true;

    List<Map<String,String>> questionAndAnswer;
    List<Map<Long,String>> emmitedAlerts;
}
