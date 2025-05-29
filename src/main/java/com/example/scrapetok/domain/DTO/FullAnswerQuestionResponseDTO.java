package com.example.scrapetok.domain.DTO;

import com.example.scrapetok.domain.enums.statusQA;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FullAnswerQuestionResponseDTO {
    private Long id;
    @Enumerated(EnumType.STRING)
    private statusQA status;
    private String questionDescription;
    private String answerDescription;
    private Long adminId;
    private Long userId;
    private LocalDate questionDate;
    private LocalTime questionHour;
    private LocalDate answerDate;
    private LocalTime  answerHour;
}
