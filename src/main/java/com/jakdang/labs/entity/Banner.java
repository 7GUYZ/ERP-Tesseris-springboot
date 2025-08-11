package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_index")
    private Integer bannerIndex;

    @Column(name = "banner_photo", length = 150)
    private String bannerPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index")
    private UserTesseris userIndex;

    @Column(name = "banner_create_time")
    private LocalDateTime bannerCreateTime;

    /**
     * 배너 표시 여부
     * 0 : 미표시 (False)
     * 1 : 표시 (True)
     */
    @Column(name = "banner_isvisible", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean bannerIsvisible;

}