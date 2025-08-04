package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaJtjRepo extends JpaRepository<Qna, Integer> {
    @Query("SELECT COUNT(q) FROM Qna q")
    Long countTotal();

    @Query("SELECT COUNT(q) FROM Qna q WHERE q.answerDesc IS NOT NULL")
    Long countAnswered();

    @Query("SELECT COUNT(q) FROM Qna q WHERE q.answerDesc IS NULL")
    Long countUnanswered();
} 