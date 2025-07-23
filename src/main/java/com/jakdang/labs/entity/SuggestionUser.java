package com.jakdang.labs.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "suggestion_user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"suggestion_user_index", "recommendation_user_index"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "suggestion_user_id") 
    private Long suggestionUserId;

    @Column(name = "suggestion_user_index", nullable = false)
    private Integer suggestionUserIndex;

    @Column(name = "recommendation_user_index", nullable = false)
    private Integer recommendationUserIndex;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;
}
