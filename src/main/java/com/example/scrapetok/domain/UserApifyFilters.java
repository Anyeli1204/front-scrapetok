package com.example.scrapetok.domain;

import com.example.scrapetok.domain.enums.ApifyRunStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserApifyFilters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historial_id", nullable = false)
    private UserApifyCallHistorial historial;

    // Cantidad de post por profile, hashtag o keyword -> Default: 1
    private Integer nlastPostByHashtags;

    @Column(columnDefinition = "TEXT")
    private String hashtags;

    @Column(columnDefinition = "TEXT")
    private String keyWords;

    @Column(columnDefinition = "TEXT")
    private String tiktokAccount;
    private String dateFrom;
    private String dateTo;


    @Enumerated(EnumType.STRING)
    private ApifyRunStatus apifyRunStatus = ApifyRunStatus.FAILED;
    private Integer executionTime;
}
