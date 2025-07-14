package com.jakdang.labs.api.notice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_index")
    private Integer noticeIndex;

    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "notice_title", length = 150)
    private String noticeTitle;

    @Column(name = "notice_desc", columnDefinition = "TEXT")
    private String noticeDesc;

    @Column(name = "notice_create_time")
    private LocalDateTime noticeCreateTime;
} 