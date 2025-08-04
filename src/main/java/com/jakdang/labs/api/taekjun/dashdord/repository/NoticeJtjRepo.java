package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeJtjRepo extends JpaRepository<Notice, Integer> {
    @Query("SELECT n FROM Notice n ORDER BY n.noticeCreateTime DESC")
    List<Notice> findRecentNotices(Pageable pageable);
} 