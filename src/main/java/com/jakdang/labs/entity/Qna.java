package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_index")
    private Integer qnaIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_user_index")
    private UserTesseris questionUser;

    @Column(name = "question_title", length = 150)
    private String questionTitle;

    @Column(name = "question_desc", columnDefinition = "TEXT")
    private String questionDesc;

    @Column(name = "answer_user_index")
    private Integer answerUserIndex;

    @Column(name = "answer_title", length = 150)
    private String answerTitle;

    @Column(name = "answer_desc", columnDefinition = "TEXT")
    private String answerDesc;

    @Column(name = "qna_create_time")
    private LocalDateTime qnaCreateTime;

    @Column(name = "answer_create_time")
    private LocalDateTime answerCreateTime;
} 