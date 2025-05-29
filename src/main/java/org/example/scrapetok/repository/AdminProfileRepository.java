package org.example.scrapetok.repository;

import com.example.scrapetok.domain.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile,Long> {
}
