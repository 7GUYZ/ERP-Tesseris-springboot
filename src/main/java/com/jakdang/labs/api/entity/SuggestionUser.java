package com.jakdang.labs.api.entity;


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
    uniqueConstraints = @UniqueConstraint(columnNames = {"suggestionUserIndex", "recommendationUserIndex"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suggestionUserId;

    @Column(nullable = false)
    private Integer suggestionUserIndex;

    @Column(nullable = false)
    private Integer recommendationUserIndex;

    @Column(nullable = false)
    private LocalDateTime joinDate;
}
