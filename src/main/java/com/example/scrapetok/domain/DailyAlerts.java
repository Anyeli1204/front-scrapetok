package com.example.scrapetok.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class DailyAlerts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminProfile admin;


    @Column(nullable = false)
    private String subject;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;
    @Column(nullable = false)
    private LocalDate postedDate;
    @Column(nullable = false)
    private LocalTime postedTime;

    @ManyToMany
    @JoinTable(
            name = "alert_user",
            // Dueño de la relación
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<GeneralAccount> userEmails = new HashSet<>();
}
