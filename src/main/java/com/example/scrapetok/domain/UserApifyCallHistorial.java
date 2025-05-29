package com.example.scrapetok.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(exclude = "user")

@Data
public class UserApifyCallHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;


    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private GeneralAccount user;

    // Pensamos expandir en un futuro el historial ...

    @OneToMany(mappedBy = "historial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserApifyFilters> filtros= new ArrayList<>();

}
