package com.example.scrapetok.domain.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserFiltersRequestDTO {
    @NotNull
    private Long userId;
    // Separador en string -> ','
    private String hashtags;
    // Separador en string -> ','
    private String keyWords;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The date must follow the format YYYY-MM-DD")
    private String dateFrom;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The date must follow the format YYYY-MM-DD")
    private String dateTo;
    private Integer nlastPostByHashtags;

    // Separador en string -> ','
    private String tiktokAccount;

}
