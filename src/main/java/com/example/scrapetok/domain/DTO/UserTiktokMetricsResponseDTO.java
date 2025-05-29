package com.example.scrapetok.domain.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UserTiktokMetricsResponseDTO {
    private Long    id;
    private Long    userId;
    private String  postId;
    private LocalDate datePosted;
    private LocalTime hourPosted;
    private String  usernameTiktokAccount;
    private String  postURL;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer saves;
    private Integer reposts;
    private Integer totalInteractions;
    private Double  engagement;
    private Integer numberHashtags;
    private String  hashtags;
    private String  soundId;
    private String  soundURL;
    private String  regionPost;
    private LocalDate dateTracking;
    private LocalTime timeTracking;

    // getters y setters (o @Data de Lombok)
}