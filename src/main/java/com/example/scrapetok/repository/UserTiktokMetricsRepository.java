package com.example.scrapetok.repository;

import com.example.scrapetok.domain.UserTiktokMetrics;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserTiktokMetricsRepository extends JpaRepository<UserTiktokMetrics,Long>, JpaSpecificationExecutor<UserTiktokMetrics> {
    List<UserTiktokMetrics> findUsernameTiktokAccountByUserId(Long userId);
    List<UserTiktokMetrics> findAll(Specification<UserTiktokMetrics> spec, Sort sort);
}
