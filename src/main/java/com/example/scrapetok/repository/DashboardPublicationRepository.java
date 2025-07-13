package com.example.scrapetok.repository;

import com.example.scrapetok.domain.DashboardPublishedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardPublicationRepository extends JpaRepository<DashboardPublishedData, Long> {

}
