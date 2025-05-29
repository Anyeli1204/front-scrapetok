package com.example.scrapetok.repository;

import com.example.scrapetok.domain.UserApifyFilters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserApifyFilterRepository extends JpaRepository<UserApifyFilters, Long> {

}
