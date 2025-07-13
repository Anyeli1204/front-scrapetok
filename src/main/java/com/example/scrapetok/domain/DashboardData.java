package com.example.scrapetok.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class DashboardData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = false)
    private String usedHashTag;

    @Column(nullable = false)
    private LocalDate datePosted;

    @Column(nullable = false)
    private String usernameTiktokAccount;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String postURL;

    @Column(nullable = false)
    private Integer views;
    @Column(nullable = false)
    private Integer likes;
    @Column(nullable = false)
    private Double engagement;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private DashboardPublishedData publishedData;

}
