package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
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