package com.jakdang.labs.global;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate(){
        // 한국 시간으로 설정
        ZonedDateTime koreanTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.createdAt = koreanTime.toInstant();
        this.updatedAt = koreanTime.toInstant();
    }

    @PreUpdate
    protected void onUpdate(){
        // 한국 시간으로 설정
        ZonedDateTime koreanTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.updatedAt = koreanTime.toInstant();
    }
}
