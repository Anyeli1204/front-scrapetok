package com.example.scrapetok.domain.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TopGlobalEmailDTO {
    @NotNull
    private Long adminId;
    @NotBlank
    private String usedHashTag;
    @NotNull
    private LocalDate datePosted;
    @NotBlank
    private String usernameTiktokAccount;
    @NotBlank
    private String postURL;
    @NotNull
    @Min(value = 1, message = "At least 1 view must be requested")
    private Integer views;
    @NotNull
    @Min(value = 1, message = "At least 1 like must be requested")
    private Integer likes;
    @NotNull
    private Double engagement;
}
