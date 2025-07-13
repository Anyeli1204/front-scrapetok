package com.example.scrapetok.repository;


import com.example.scrapetok.domain.DashboardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardDataRepository extends JpaRepository<DashboardData,Long> {
    @Query("""
        SELECT d
        FROM DashboardData d
        WHERE d.publishedData.id = (
            SELECT MAX(dd.publishedData.id)
            FROM DashboardData dd
        )
        """)
    List<DashboardData> findAllWithMaxPublicationId();
}
