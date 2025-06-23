package com.example.scrapetok.repository;

import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralAccountRepository extends JpaRepository<GeneralAccount, Long> {
    Optional<GeneralAccount> findByEmail(String email);
}
