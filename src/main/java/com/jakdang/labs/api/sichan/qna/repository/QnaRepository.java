package com.jakdang.labs.api.sichan.qna.repository;

import com.jakdang.labs.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Integer> {

    // 사용자별 QnA 목록 조회
    @Query("SELECT q FROM Qna q LEFT JOIN q.questionUser u WHERE u.userIndex = :userIndex ORDER BY q.qnaCreateTime DESC")
    List<Qna> findByQuestionUserIndex(@Param("userIndex") Integer userIndex);

    // 사용자별 QnA 목록 조회 (간단한 버전)
    List<Qna> findByQuestionUser_UserIndexOrderByQnaCreateTimeDesc(Integer userIndex);

    // QnA 상세 조회 (사용자 본인 것만)
    @Query("SELECT q FROM Qna q LEFT JOIN q.questionUser u WHERE q.qnaIndex = :qnaIndex AND u.userIndex = :userIndex")
    Optional<Qna> findByQnaIndexAndQuestionUserIndex(@Param("qnaIndex") Integer qnaIndex,
            @Param("userIndex") Integer userIndex);

    // 모든 QnA 목록 조회 (관리자용)
    @Query("SELECT q FROM Qna q LEFT JOIN q.questionUser qu ORDER BY q.qnaCreateTime DESC")
    List<Qna> findAllWithUsers();
}