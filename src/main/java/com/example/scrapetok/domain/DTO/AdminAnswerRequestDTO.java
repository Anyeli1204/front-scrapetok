package com.example.scrapetok.domain.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminAnswerRequestDTO {
    @NotNull
    private Long questionId;
    @NotNull
    private Long adminId;
    @NotBlank
    private String answerDescription;
}
