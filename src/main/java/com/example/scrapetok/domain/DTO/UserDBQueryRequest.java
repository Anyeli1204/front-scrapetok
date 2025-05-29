package com.example.scrapetok.domain.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDBQueryRequest {
    @NotNull
    private Long userId;

    // Separador para todos los string: ','
    private String tiktokUsernames;
    private String postId;
    private LocalDate datePostedFrom;
    private LocalDate datePostedTo;
    private String postURL;

    private Integer minViews;
    private Integer maxViews;

    private Integer minLikes;
    private Integer maxLikes;

    private Integer minTotalInteractions;
    private Integer maxTotalInteractions;

    private Double minEngagement;
    private Double maxEngagement;

    private String hashtags;
    private String soundId;
    private String soundURL;
    private String regionPost;


}
