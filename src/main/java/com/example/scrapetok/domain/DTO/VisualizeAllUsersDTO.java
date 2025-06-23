package com.example.scrapetok.domain.DTO;

import com.example.scrapetok.domain.enums.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VisualizeAllUsersDTO {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private LocalDate creationDate;
    private Role role;
}
