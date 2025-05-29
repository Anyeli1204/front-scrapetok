package com.example.scrapetok.domain.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserQuestionRequestDTO {
    @NotNull
    private Long userId;
    @NotBlank
    private String questionDescription;
}
