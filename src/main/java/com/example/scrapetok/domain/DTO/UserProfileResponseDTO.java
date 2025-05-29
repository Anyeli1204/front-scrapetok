package com.example.scrapetok.domain.DTO;

import com.example.scrapetok.domain.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserProfileResponseDTO {

    // Información personal
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate creationDate;

    // Información de su historial
    private Integer amountScrappedAccount;
    List<Map<Object,Object>> filters;
    Set<String> tiktokUsernameScraped;

}
