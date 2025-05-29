package com.example.scrapetok.domain;

import com.example.scrapetok.domain.enums.statusQA;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class QuestAndAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private GeneralAccount user;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = true) // Puede ser null si pregunta aún no se responde
    private AdminProfile admin;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private statusQA status = statusQA.PENDING;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String questionDescription;
    @Column(nullable = false)
    private LocalDate questionDate;
    @Column(nullable = false)
    private LocalTime questionHour;
    @Column(columnDefinition = "TEXT")
    private String answerDescription;
    private LocalDate answerDate;
    private LocalTime  answerHour;
}
