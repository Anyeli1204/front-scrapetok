package com.example.scrapetok.repository;

import com.example.scrapetok.domain.DailyAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyAlertsRepository extends JpaRepository<DailyAlerts, Integer> {
}
