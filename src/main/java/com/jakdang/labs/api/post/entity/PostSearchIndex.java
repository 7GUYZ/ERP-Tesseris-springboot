package com.jakdang.labs.api.post.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_search_index",
       indexes = {
           @Index(name = "idx_search_title", columnList = "title"),
           @Index(name = "idx_search_content", columnList = "content"),
           @Index(name = "idx_search_author", columnList = "authorName"),
           @Index(name = "idx_search_community", columnList = "communityId"),
           @Index(name = "idx_search_board", columnList = "boardId"),
           @Index(name = "idx_search_composite", columnList = "communityId, boardId")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchIndex {
    
    @Id
    @Column(name = "post_id", length = 50)
    private String postId;
    
    @Column(name = "title", length = 500)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "author_name", length = 100)
    private String authorName;
    
    @Column(name = "community_id", length = 50)
    private String communityId;
    
    @Column(name = "board_id")
    private String boardId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 