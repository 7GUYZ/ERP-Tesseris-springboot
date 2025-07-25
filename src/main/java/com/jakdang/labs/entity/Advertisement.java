package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advertisement_index")
    private Integer advertisementIndex;

    @Column(name = "advertisement_photo", length = 500)
    private String advertisementPhoto;

    @Column(name = "advertisement_url", length = 500)
    private String advertisementUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "advertisement_create_time")
    private LocalDateTime advertisementCreateTime;
} 