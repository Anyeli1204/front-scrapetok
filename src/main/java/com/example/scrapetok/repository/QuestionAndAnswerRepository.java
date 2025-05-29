package com.example.scrapetok.repository;

import com.example.scrapetok.domain.QuestAndAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAndAnswerRepository extends JpaRepository<QuestAndAnswer,Long> {
}
