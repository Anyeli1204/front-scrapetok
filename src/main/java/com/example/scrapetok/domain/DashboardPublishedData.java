package com.example.scrapetok.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class DashboardPublishedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "publishedData", cascade = CascadeType.ALL)
    private List<DashboardData> data = new ArrayList<>();
}
